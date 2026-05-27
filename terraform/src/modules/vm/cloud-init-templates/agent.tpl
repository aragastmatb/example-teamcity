#cloud-config
# TeamCity Agent: Ubuntu + Docker + jetbrains/teamcity-agent контейнер
# ============================================================

hostname: ${vm_hostname}
timezone: ${timezone}

users:
  - name: ${user_name}
    sudo: ${user_sudo}
    shell: ${user_shell}
    ssh_authorized_keys:
      - ${ssh_public_key}
    lock_passwd: true

package_update: ${package_update}
package_upgrade: ${package_upgrade}
packages: ${packages}

runcmd:
  # Установка Docker (аналогично серверу)
  - [ 'apt-get', 'install', '-y', 'ca-certificates', 'curl', 'gnupg', 'lsb-release' ]
  - [ 'sh', '-c', 'install -m 0755 -d /etc/apt/keyrings && curl -fsSL https://download.docker.com/linux/ubuntu/gpg | gpg --dearmor -o /etc/apt/keyrings/docker.gpg' ]
  - [ 'sh', '-c', 'echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" > /etc/apt/sources.list.d/docker.list' ]
  - [ 'apt-get', 'update', '-qq' ]
  - [ 'apt-get', 'install', '-y', '-qq', 'docker-ce', 'docker-ce-cli', 'containerd.io', 'docker-compose-plugin' ]
  - [ 'usermod', '-aG', 'docker', '${user_name}' ]
  
  # 🔑 КРИТИЧЕСКИ: переменная окружения SERVER_URL для подключения к серверу
  # По заданию: SERVER_URL: "http://<teamcity_url>:8111"
  - [ 'sh', '-c', 'echo "SERVER_URL=${server_url}" >> /etc/environment' ]
  
  # Запуск TeamCity Agent контейнера из Docker Hub
  # Образ: ${docker_image} (jetbrains/teamcity-agent:latest)
  # ENV: SERVER_URL (обязательно!), TEAMCITY_AGENT_NAME (опционально)
  - [ 'sh', '-c', 'docker run -d --name ${container_name} --restart=unless-stopped -e SERVER_URL=${server_url} -e TEAMCITY_AGENT_NAME=${vm_hostname}-agent ${docker_image}' ]
  
  # Логирование
  - [ 'sh', '-c', 'echo "[$(date)] TeamCity Agent container started: ${docker_image}" >> /var/log/teamcity-init.log' ]
  - [ 'sh', '-c', 'echo "[$(date)] Connected to: ${server_url}" >> /var/log/teamcity-init.log' ]