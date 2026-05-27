#cloud-config
hostname: ${vm_hostname}
fqdn: ${vm_hostname}
timezone: ${timezone}
users:
  - name: ${user_name}
    sudo: ${user_sudo}
    shell: ${user_shell}
    ssh_authorized_keys:
      - ${ssh_public_key}
package_update: ${package_update}
packages: ${jsonencode(packages)}
package_upgrade: ${package_upgrade}
