# ============================================================================
# ВЫХОДНЫЕ ДАННЫЕ: Информация для пользователя и автоматизации
# Эти значения выводятся после terraform apply и доступны через terraform output
# ============================================================================

# ----------------- VPC И СЕТЬ -----------------
output "vpc_network_id" {
  description = "ID сети VPC для использования в других конфигурациях"
  value       = module.vpc.network_id
}

output "vpc_subnet_id" {
  description = "ID подсети для размещения дополнительных ресурсов"
  value       = module.vpc.subnet_id
}

output "vpc_cidr" {
  description = "CIDR блок подсети для правил безопасности"
  value       = var.v4_cidr_blocks
}

# ----------------- БЕЗОПАСНОСТЬ -----------------
output "security_group_id" {
  description = "ID группы безопасности для привязки к новым ВМ"
  value       = module.security.security_group_id
}

# ----------------- TEAMCITY SERVER -----------------
output "teamcity_server" {
  description = "Полная информация о ВМ TeamCity Server"
  value = {
    hostname    = module.teamcity_server.vm_hostname
    external_ip = module.teamcity_server.external_ip
    internal_ip = module.teamcity_server.internal_ip
    # Готовый URL для доступа к веб-интерфейсу
    web_url     = "http://${module.teamcity_server.external_ip}:8111"
  }
}

# ----------------- TEAMCITY AGENT -----------------
output "teamcity_agent" {
  description = "Полная информация о ВМ TeamCity Agent"
  value = {
    hostname    = module.teamcity_agent.vm_hostname
    external_ip = module.teamcity_agent.external_ip
    internal_ip = module.teamcity_agent.internal_ip
    # URL сервера для проверки подключения
    server_url  = "http://${module.teamcity_server.internal_ip}:8111"
  }
}

# ----------------- NEXUS REPOSITORY -----------------
output "nexus" {
  description = "Полная информация о ВМ Nexus Repository"
  value = {
    hostname    = module.nexus.vm_hostname
    external_ip = module.nexus.external_ip
    internal_ip = module.nexus.internal_ip
    web_url     = "http://${module.nexus.external_ip}:8081"
    # URL для настройки Maven distributionManagement
    maven_url   = "http://${module.nexus.internal_ip}:8081/repository/maven-releases/"
  }
}

# ----------------- ANSIBLE БЫСТРЫЙ СТАРТ -----------------
output "ansible_quick_start" {
  description = "Команды для быстрого подключения через Ansible"
  value = <<-EOT
    # 🔍 Проверка связи со всеми хостами:
    ansible ci_infrastructure -i ${var.ansible_inventory_path} -m ping -u ${var.ssh_user}
    
    # 🎯 Запуск полной настройки:
    ansible-playbook -i ${var.ansible_inventory_path} playbooks/site.yml
    
    # 🔧 Настройка конкретного компонента:
    ansible-playbook -i ${var.ansible_inventory_path} -l teamcity_servers playbooks/configure-teamcity.yml
    
    # 🔐 Подключение по SSH к конкретному хосту:
    ssh ${var.ssh_user}@${module.teamcity_server.external_ip}  # Server
    ssh ${var.ssh_user}@${module.nexus.external_ip}            # Nexus
    ssh ${var.ssh_user}@${module.teamcity_agent.external_ip}   # Agent
  EOT
}

# ----------------- ПОШАГОВАЯ ИНСТРУКЦИЯ -----------------
output "next_steps" {
  description = "Пошаговая инструкция по завершению настройки после создания инфраструктуры"
  value = <<-EOT
    🎯 ИНСТРУКЦИЯ ПО ЗАВЕРШЕНИЮ НАСТРОЙКИ TEAMCITY
    
    1️⃣  ОТКРОЙТЕ TEAMCITY:
        → URL: ${module.teamcity_server.external_ip}:8111
        → Создайте пользователя-администратора
        → Введите лицензионный ключ или нажмите "Start Trial"
    
    2️⃣  АВТОРИЗУЙТЕ BUILD AGENT:
        → В TeamCity: Administration → Agents
        → Найдите нового агента (статус: Unauthorized)
        → Нажмите "Authorize" для подключения
        → Агент должен подключиться автоматически благодаря SERVER_URL
    
    3️⃣  НАСТРОЙТЕ NEXUS REPOSITORY:
        → Откройте: ${module.nexus.external_ip}:8081
        → Логин: admin / Пароль: ${var.nexus_admin_password != "" ? "***" : "admin123"}
        → ⚠️ Смените пароль при первом входе!
        → Создайте пользователя ci-deployer с ролями:
          • nx-repository-view-maven2-*-read
          • nx-repository-view-maven2-*-add
          • nx-repository-view-maven2-*-edit
    
    4️⃣  ДОБАВЬТЕ ПАРАМЕТРЫ В TEAMCITY:
        Project Settings → Parameters:
          • nexus.user = ci-deployer
          • nexus.password = ******** (тип: Password — скрытое значение)
          • nexus.url = ${module.nexus.internal_ip}:8081
    
    5️⃣  ИМПОРТИРУЙТЕ ПРОЕКТ:
        → Create Project → From URL
        → Укажите ссылку на ваш fork: https://github.com/ваш_логин/example-teamcity
        → Включите "Versioned Settings" → Format: Kotlin DSL
        → Нажмите "Enable" — конфигурация загрузится из .teamcity/
    
    6️⃣  ЗАПУСТИТЕ ПЕРВУЮ СБОРКУ:
        → В ветке master: выполнится "mvn clean deploy"
        → В фича-ветке: выполнится "mvn clean test"
        → Артефакты появятся в Nexus и во вкладке Artifacts сборки
    
    🚀 ГОТОВО! Ваш CI/CD пайплайн работает!
  EOT
  sensitive = true
}