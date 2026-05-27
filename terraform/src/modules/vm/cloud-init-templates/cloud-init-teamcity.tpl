#!/bin/bash
# ============================================================================
# CLOUD-INIT TEMPLATE: Инициализация TeamCity Server
# Файл обрабатывается Terraform function templatefile() перед отправкой в ВМ
# Все переменные вида ${var_name} подставляются на этапе terraform apply
# ============================================================================

# Выход при первой ошибке, незадекларированных переменных и ошибках в пайпах
set -euo pipefail

# Перенаправляем весь вывод (stdout + stderr) в лог cloud-init для отладки
exec > >(tee -a /var/log/cloud-init-teamcity.log) 2>&1

log() {
    echo "[$(date +'%Y-%m-%d %H:%M:%S')] $*"
}

log "🚀 Запуск cloud-init инициализации TeamCity Server..."

# ============================================================================
# ПЕРЕМЕННЫЕ, ПОДСТАВЛЯЕМЫЕ TERRAFORM
# Значения передаются через templatefile() в main.tf
# ============================================================================
SERVER_URL="${server_url}"
NEXUS_URL="${nexus_url}"

# ============================================================================
# 1. ОБНОВЛЕНИЕ СИСТЕМЫ И УСТАНОВКА БАЗОВЫХ ПАКЕТОВ
# ============================================================================
log "📦 Обновление пакетов и установка утилит..."
export DEBIAN_FRONTEND=noninteractive
apt-get update -qq
apt-get upgrade -y -qq
apt-get install -y -qq \
    curl wget git unzip jq apt-transport-https ca-certificates \
    software-properties-common

# ============================================================================
# 2. УСТАНОВКА JAVA (если отсутствует или версия < 11)
# ============================================================================
if ! command -v java &> /dev/null || ! java -version 2>&1 | grep -qE "1[1-9]|2[0-9]"; then
    log "☕ Установка OpenJDK 17..."
    add-apt-repository ppa:openjdk-r/ppa -y 2>/dev/null || true
    apt-get update -qq
    apt-get install -y -qq openjdk-17-jdk-headless
    update-alternatives --set java /usr/lib/jvm/java-17-openjdk-amd64/bin/java
fi

# ============================================================================
# 3. НАСТРОЙКА TEAMCITY SERVER
# ============================================================================
TEAMCITY_HOME="/opt/teamcity"
TEAMCITY_DATA="/var/lib/teamcity"

log "⚙️ Настройка директорий и пользователя TeamCity..."
# Создаём пользователя, если он не существует (образ может не иметь его)
id -u teamcity &>/dev/null || useradd -r -s /usr/sbin/nologin -d "$TEAMCITY_DATA" teamcity

mkdir -p "$TEAMCITY_DATA"/{config,logs,system} "$TEAMCITY_HOME"/conf
chown -R teamcity:teamcity "$TEAMCITY_DATA" "$TEAMCITY_HOME"

log "🔧 Настройка JVM-параметров под 4GB RAM..."
cat > "$TEAMCITY_HOME/conf/teamcity-server.vmoptions" <<EOF
-Xms1024m
-Xmx2048m
-XX:ReservedCodeCacheSize=320m
-XX:+UseG1GC
-Dteamcity.data.path=$TEAMCITY_DATA
-Djava.awt.headless=true
EOF

# ============================================================================
# 4. ИНТЕГРАЦИЯ С NEXUS (Maven settings для будущих сборок)
# ============================================================================
if [[ -n "$NEXUS_URL" ]]; then
    log "🔗 Настройка Maven settings для интеграции с Nexus: $NEXUS_URL"
    mkdir -p "$TEAMCITY_HOME/.m2"
    cat > "$TEAMCITY_HOME/.m2/settings.xml" <<EOF
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0">
  <servers>
    <server>
      <id>nexus</id>
      <username>\${env.NEXUS_USER}</username>
      <password>\${env.NEXUS_PASSWORD}</password>
    </server>
  </servers>
  <profiles>
    <profile>
      <id>nexus</id>
      <repositories>
        <repository>
          <id>nexus-public</id>
          <url>${NEXUS_URL}/repository/maven-public/</url>
          <releases><enabled>true</enabled></releases>
          <snapshots><enabled>true</enabled></snapshots>
        </repository>
      </repositories>
    </profile>
  </profiles>
  <activeProfiles>
    <activeProfile>nexus</activeProfile>
  </activeProfiles>
</settings>
EOF
    chown -R teamcity:teamcity "$TEAMCITY_HOME/.m2"
fi

# ============================================================================
# 5. АВТОЗАПУСК И ПРОВЕРКА ГОТОВНОСТИ
# ============================================================================
log "🔄 Настройка автозапуска TeamCity..."
if [[ -f /etc/systemd/system/teamcity.service ]]; then
    systemctl daemon-reload
    systemctl enable teamcity.service
    systemctl start teamcity.service
    log "✅ Сервис teamcity.service запущен"
fi

log "⏳ Ожидание готовности Web UI ($SERVER_URL)..."
MAX_ATTEMPTS=60
SLEEP_INTERVAL=10
for i in $(seq 1 $MAX_ATTEMPTS); do
    if curl -sf "$SERVER_URL" > /dev/null 2>&1; then
        log "✅ TeamCity успешно запущен и доступен по $SERVER_URL"
        break
    fi
    log "  Попытка $i/$MAX_ATTEMPTS... ожидаю ${SLEEP_INTERVAL}с"
    sleep $SLEEP_INTERVAL
done

log "🎉 Cloud-init инициализация завершена!"
log "📋 Дальнейшая настройка выполняется через Ansible или вручную:"
log "  1. Откройте $SERVER_URL в браузере"
log "  2. Завершите первоначальную настройку (создание админа)"
log "  3. Авторизуйте Build Agent в Administration → Agents"