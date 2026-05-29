#cloud-config
# Минимальный шаблон для ВМ без Docker (например, для Nexus через Ansible)
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
  # Логирование инициализации
  - [ 'sh', '-c', 'echo "[$(date)] Generic VM ${vm_hostname} initialized" >> /var/log/teamcity-init.log' ]
  
  # Установка базовых пакетов (если указаны в install_packages)
  # Дополнительные команды можно добавить через cloud_init_vars