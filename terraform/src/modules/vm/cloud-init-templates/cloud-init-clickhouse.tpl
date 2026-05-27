#cloud-config

users:
  - name: yc-user
    groups: sudo, docker
    shell: /bin/bash
    sudo: ['ALL=(ALL) NOPASSWD:ALL']
    ssh_authorized_keys:
      - ${ssh_public_key}

runcmd:
  # Переход в директорию с конфигурациями репозиториев
  - cd /etc/yum.repos.d/

  # Отключаем поиск зеркал и переходим на прямые ссылки vault.centos.org для основных репозиториев CentOS
  - sed -i 's/mirrorlist=http/#mirrorlist=http/g' CentOS-*.repo
  - sed -i 's/^#baseurl=http:\/\/mirror.centos.org/baseurl=http:\/\/vault.centos.org/g' CentOS-*.repo
  - sed -i 's/mirror.centos.org/vault.centos.org/g' CentOS-*.repo

  # Установка EPEL, если ещё не установлен
  - yum install -y epel-release

  # Настройка репозитория EPEL для использования архива
  - sed -i 's/mirrorlist=http/#mirrorlist=http/g' epel.repo
  - sed -i 's/^#baseurl=http:\/\/download.fedoraproject.org\/pub\/epel/baseurl=https:\/\/archives.fedoraproject.org\/pub\/archive\/epel/g' epel.repo

  # Установка плагина priorities
  - yum install -y yum-plugin-priorities

  # Настройка приоритетов для репозиториев в CentOS-Base.repo
  - |
    if [ -f "/etc/yum.repos.d/CentOS-Base.repo" ]; then
      # Добавляем приоритеты для основных репозиториев
      sed -i '/\[base\]/a priority=1' /etc/yum.repos.d/CentOS-Base.repo
      sed -i '/\[updates\]/a priority=1' /etc/yum.repos.d/CentOS-Base.repo
      sed -i '/\[extras\]/a priority=2' /etc/yum.repos.d/CentOS-Base.repo
      # Дополнительно можно установить приоритет для centosplus (если используется)
      sed -i '/\[centosplus\]/a priority=3' /etc/yum.repos.d/CentOS-Base.repo 2>/dev/null || true
    fi

  # Отключение проблемных репозиториев (опционально — пример для отключённых репозиториев)
  - yum-config-manager --disable '*.src' 2>/dev/null || true
  - yum-config-manager --disable 'centos-extras' 2>/dev/null || true

  # Отключение ненужных плагинов yum (оставляем только priorities)
  - for plugin in fastestmirror versionlock; do
      if [ -f "/etc/yum/pluginconf.d/$${plugin}.conf" ]; then
        sed -i 's/enabled=1/enabled=0/g' "/etc/yum/pluginconf.d/$${plugin}.conf"
      fi
    done

  # Проверка корректности конфигурации priorities
  - grep -E "(base|updates|extras|centosplus).*priority" /etc/yum.repos.d/CentOS-Base.repo || echo "Priorities configuration check completed"

  # Очистка кэша yum для применения изменений
  - yum clean all

  # Проверка доступности репозиториев и обновление списка пакетов
  - yum makecache fast

package_update: true
package_upgrade: false

final_message: "Cloud-init completed: CentOS 7 repositories migrated to Vault/Archive with priorities plugin configured and user 'yc-user' created"
