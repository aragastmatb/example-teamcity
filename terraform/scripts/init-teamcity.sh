#!/bin/bash
# ============================================================================
# CLOUD-INIT СКРИПТ: Базовая инициализация TeamCity Server
# Выполняется автоматически при первом запуске ВМ в Yandex Cloud
# ============================================================================

# Выход при любой ошибке (fail-fast)
set -euo pipefail

# Логирование всех команд в файл для отладки
exec > >(tee -a /var/log/cloud-init-output.log) 2>&1

# Параметры, подставленные через Terraform templatefile
SERVER_URL="${server_url}"
NEXUS_URL="${nexus_url}"

log() {
    echo "[$(date +'%Y-%m-%d %H:%M:%S')] $*"
}

log "🚀 Начало инициализации TeamCity Server..."

# ----------------- 1. ОБНОВЛЕНИЕ СИСТЕМЫ -----------------
log "📦 Обновление пакетов..."
apt-get update -qq
apt-get upgrade -y -qq
apt-get install -y -qq \
    curl \
    wget \
    git \
    unzip \
    jq \
    apt-transport-https \
    ca-certificates

# ----------------- 2. УСТАНОВКА JAVA (если не предустановлена в образе) -----------------
if ! command -v java &> /dev/null || ! java -version 2>&1 | grep -q "17\|11"; then
    log "☕ Установка OpenJDK 17..."
    apt-get install -y -qq openjdk-17-jdk-headless
    update-alternatives --set java /usr/lib/jvm/java-17-openjdk-amd64/bin/java
fi

# ----------------- 3. НАСТРОЙКА TEAMCITY SERVER -----------------
TEAMCITY_HOME="/opt/teamcity"
TEAMCITY_DATA="/var/lib/teamcity"

log "⚙️ Настройка TeamCity..."

# Создаём директорию для данных (конфигурация, плагины, логи)
mkdir -p "$TEAMCITY_DATA"
chown -R teamcity:teamcity "$TEAMCITY_DATA" 2>/dev/null || true

# Настраиваем JVM-параметры для TeamCity (оптимизация под 4GB RAM)
cat > /opt/teamcity/conf/teamcity-server.vmoptions <<EOF
# Custom JVM options for TeamCity Server
-Xmx2048m
-Xms1024m
-XX:ReservedCodeCacheSize=320m
-XX:+UseG1GC
-Dteamcity.data.path=$TEAMCITY_DATA
-Djava.awt.headless=true
EOF

# ----------------- 4. НАСТРОЙКА PROXY ДЛЯ NEXUS (опционально) -----------------
if [[ -n "$NEXUS_URL" ]]; then
    log "🔗 Настройка интеграции с Nexus: $NEXUS_URL"
    
    # Создаём файл с настройками для Maven (будет использован TeamCity)
    mkdir -p /opt/teamcity/.m2
    cat > /opt/teamcity/.m2/settings.xml <<EOF
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
        </repository>
      </repositories>
    </profile>
  </profiles>
  <activeProfiles>
    <activeProfile>nexus</activeProfile>
  </activeProfiles>
</settings>
EOF
    chown -R teamcity:teamcity /opt/teamcity/.m2
fi

# ----------------- 5. АВТОЗАПУСК TEAMCITY -----------------
log "🔄 Настройка автозапуска TeamCity..."

# Если в образе есть systemd-сервис — включаем его
if [[ -f /etc/systemd/system/teamcity.service ]]; then
    systemctl daemon-reload
    systemctl enable teamcity.service
    systemctl start teamcity.service
    log "✅ TeamCity service запущен"
fi

# ----------------- 6. ПРОВЕРКА ГОТОВНОСТИ -----------------
log "⏳ Ожидание готовности TeamCity Web UI..."
for i in {1..60}; do
    if curl -sf "${SERVER_URL}" > /dev/null 2>&1; then
        log "✅ TeamCity доступен по адресу: ${SERVER_URL}"
        break
    fi
    log "  Попытка $i/60... ждём 10 сек"
    sleep 10
done

log "🎉 Инициализация завершена!"
log "📋 Следующие шаги:"
log "  1. Откройте ${SERVER_URL} в браузере"
log "  2. Завершите первоначальную настройку (создание админа)"
log "  3. Запустите Ansible для финальной конфигурации"

exit 0