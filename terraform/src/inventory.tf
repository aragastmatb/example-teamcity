# ============================================================================
# ГЕНЕРАЦИЯ ANSIBLE INVENTORY
# Создаёт YAML-файл с информацией о хостах для последующей настройки
# ============================================================================

resource "local_file" "ansible_inventory" {
  # Создаём файл только если включён флаг run_ansible
  count = var.run_ansible ? 1 : 0
  
  # Путь к файлу инвентаря
  filename = var.ansible_inventory_path
  
  # Генерируем содержимое из шаблона
  content = templatefile("${path.module}/templates/inventory.yml.tftpl", {
    # Передаём информацию о каждом хосте
    teamcity_server = {
      hostname = module.teamcity_server.vm_hostname
      ansible_host = module.teamcity_server.external_ip  # Публичный для SSH
      internal_ip = module.teamcity_server.internal_ip   # Приватный для внутренней связи
      ansible_user = var.ssh_user
      ansible_ssh_private_key_file = var.ansible_ssh_key_path
      vm_role = "server"
      teamcity_url = "http://${module.teamcity_server.external_ip}:8111"
    }
    
    teamcity_agent = {
      hostname = module.teamcity_agent.vm_hostname
      ansible_host = module.teamcity_agent.external_ip
      internal_ip = module.teamcity_agent.internal_ip
      ansible_user = var.ssh_user
      ansible_ssh_private_key_file = var.ansible_ssh_key_path
      vm_role = "agent"
      server_url = "http://${module.teamcity_server.internal_ip}:8111"
    }
    
    nexus = {
      hostname = module.nexus.vm_hostname
      ansible_host = module.nexus.external_ip
      internal_ip = module.nexus.internal_ip
      ansible_user = var.ssh_user
      ansible_ssh_private_key_file = var.ansible_ssh_key_path
      vm_role = "nexus"
      nexus_url = "http://${module.nexus.external_ip}:8081"
    }
    
    # Глобальные параметры для всех хостов
    environment = var.environment
    project = "teamcity"
  })
}