#cloud-config
# Nexus Repository: Ubuntu + Docker + sonatype/nexus3 контейнер
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
  - name: nexus
    system: yes
    home: /opt/nexus-data
    shell: /bin/false

package_update: ${package_update}
packages: ${packages}

runcmd:
  # Установка Docker
  - [ 'apt-get', 'install', '-y', 'ca-certificates', 'curl', 'gnupg', 'lsb-release' ]
  - [ 'sh', '-c', 'install -m 0755 -d /etc/apt/keyrings && curl -fsSL https://download.docker.com/linux/ubuntu/gpg | gpg --dearmor -o /etc/apt/keyrings/docker.gpg' ]
  - [ 'sh', '-c', 'echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" > /etc/apt/sources.list.d/docker.list' ]
  - [ 'apt-get', 'update', '-qq' ]
  - [ 'apt-get', 'install', '-y', '-qq', 'docker-ce', 'docker-ce-cli', 'containerd.io', 'docker-compose-plugin' ]
  - [ 'usermod', '-aG', 'docker', '${user_name}' ]
  
  # Создание директории для данных Nexus
  - [ 'mkdir', '-p', '/opt/nexus-data' ]
  - [ 'sh', '-c', 'chown -R 200:200 /opt/nexus-data' ]
  
  # Запуск Nexus контейнера из Docker Hub
  # Образ: ${docker_image} (sonatype/nexus3:latest)
  # Порт: 8081:8081, Volume: /opt/nexus-data:/nexus-data
  - [ 'sh', '-c', 'docker run -d --name ${container_name} --restart=unless-stopped -p ${container_port}:8081 -v /opt/nexus-data:/nexus-data -e INSTALL4J_ADD_VM_PARAMS="-Xms512m -Xmx1536m -XX:MaxDirectMemorySize=512m" ${docker_image}' ]
  
  # Логирование
  - [ 'sh', '-c', 'echo "[$(date)] Nexus container started: ${docker_image}" >> /var/log/teamcity-init.log' ]
  - [ 'sh', '-c', 'echo "[$(date)] Web UI: http://${vm_hostname}:${container_port} (admin/${nexus_admin_password})" >> /var/log/teamcity-init.log' ]