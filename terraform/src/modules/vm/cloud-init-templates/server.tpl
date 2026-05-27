#cloud-config
# TeamCity Server: Ubuntu + Docker + jetbrains/teamcity-server контейнер
# ============================================================

# Базовая настройка
hostname: ${vm_hostname}
timezone: ${timezone}

# Пользователь и SSH
users:
  - name: ${user_name}
    sudo: ${user_sudo}
    shell: ${user_shell}
    ssh_authorized_keys:
      - ${ssh_public_key}
    lock_passwd: true

# Установка пакетов
package_update: ${package_update}
package_upgrade: ${package_upgrade}
packages: ${packages}

# Установка Docker и запуск TeamCity Server
runcmd:
  # 1. Установка зависимостей для Docker
  - [ 'apt-get', 'install', '-y', 'ca-certificates', 'curl', 'gnupg', 'lsb-release' ]
  
  # 2. Добавление GPG ключа Docker
  - [ 'sh', '-c', 'install -m 0755 -d /etc/apt/keyrings && curl -fsSL https://download.docker.com/linux/ubuntu/gpg | gpg --dearmor -o /etc/apt/keyrings/docker.gpg' ]
  
  # 3. Добавление репозитория Docker
  - [ 'sh', '-c', 'echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" > /etc/apt/sources.list.d/docker.list' ]
  
  # 4. Обновление и установка Docker Engine
  - [ 'apt-get', 'update', '-qq' ]
  - [ 'apt-get', 'install', '-y', '-qq', 'docker-ce', 'docker-ce-cli', 'containerd.io', 'docker-compose-plugin' ]
  
  # 5. Добавление пользователя в группу docker
  - [ 'usermod', '-aG', 'docker', '${user_name}' ]
  
  # 6. Создание директории для данных TeamCity
  - [ 'mkdir', '-p', '/opt/teamcity-data' ]
  - [ 'chown', '-R', '${user_name}:${user_name}', '/opt/teamcity-data' ]
  
  # 7. Запуск TeamCity Server контейнера из Docker Hub
  # Образ: ${docker_image} (по умолчанию: jetbrains/teamcity-server:latest)
  # Порт: 8111:8111, Volume: /opt/teamcity-data:/data/teamcity_server/datadir
  - [ 'sh', '-c', 'docker run -d --name ${container_name} --restart=unless-stopped -p 8111:8111 -v /opt/teamcity-data:/data/teamcity_server/datadir -e TEAMCITY_SERVER_OPTS="-Dteamcity.data.path=/data/teamcity_server/datadir" ${docker_image}' ]
  
  # 8. Логирование
  - [ 'sh', '-c', 'echo "[$(date)] TeamCity Server container started: ${docker_image}" >> /var/log/teamcity-init.log' ]
  - [ 'sh', '-c', 'echo "[$(date)] Web UI: http://${vm_hostname}:8111" >> /var/log/teamcity-init.log' ]