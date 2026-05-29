# ======================================================================
# main.tf 
# ======================================================================
# ОСНОВНАЯ КОНФИГУРАЦИЯ: Оркестрация модулей VPC, Security, VM
# ======================================================================

# ==================== VPC: СЕТЬ И ПОДСЕТЬ ====================
module "vpc" {
  source = "./modules/vpc"

  network_name   = var.vpc_name
  subnet_name    = var.subnet_name
  zone           = var.default_zone
  v4_cidr_blocks = var.v4_cidr_blocks
  environment    = var.environment
}

# ==================== SECURITY GROUP ====================
module "security" {
  source = "./modules/security"

  name             = "${var.vpc_name}-sg"
  description      = "Security group for TeamCity CI/CD infrastructure"
  network_id       = module.vpc.network_id
  environment      = var.environment

  # SSH доступ: только с доверенных сетей (в prod!)
  allowed_ssh_cidr = var.allowed_ssh_cidr
  # Внутренняя коммуникация
  app_subnet_cidrs = var.v4_cidr_blocks

  # TeamCity UI: в dev можно открыть везде, в prod — ограничить
  enable_teamcity_ui = true
  teamcity_ui_cidrs  = var.environment == "prod" ? var.trusted_cidrs : ["0.0.0.0/0"]
  
  # Nexus UI: аналогично
  enable_nexus_ui = true
  nexus_ui_cidrs  = var.environment == "prod" ? var.trusted_cidrs : ["0.0.0.0/0"]
}

# ==================== ВИРТУАЛЬНЫЕ МАШИНЫ ====================

module "nexus" {
  source = "./modules/vm"

  vm_name            = "nexus-repo-vm"
  vm_hostname        = "nexus-01"
  vm_role            = "generic"

  project_label      = "teamcity"
  environment_label  = var.environment
  
  subnet_id          = module.vpc.subnet_id
  security_group_ids = [module.security.security_group_id]
  
  zone               = var.default_zone
  image_family       = var.image_family_ubuntu

  ssh_public_key     = var.ssh_public_key

  vm_cores           = 2
  vm_memory          = var.vm_memory
  vm_disk_size       = var.vm_disk_size
  preemptible        = var.preemptible
  vm_core_fraction   = var.vm_core_fraction

  install_packages   = ["unzip", "openjdk-8-jdk-headless"]

  # ✅ Явная зависимость: Nexus должен быть готов до настройки TeamCity
  depends_on         = [module.vpc, module.security]
}


module "teamcity_server" {
  source = "./modules/vm"

  # Базовые параметры
  vm_name            = "teamcity-server-vm"
  vm_hostname        = "teamcity-server"
  vm_role            = "server"

  # Метки
  project_label      = "teamcity"
  environment_label  = var.environment
  extra_labels       = { component = "ci-orchestrator" } 

  # Сеть и безопасность
  subnet_id          = module.vpc.subnet_id
  security_group_ids = [module.security.security_group_id]
  
  # Зона и образ
  zone               = var.default_zone
  image_family       = var.image_family_ubuntu

  # Ресурсы
  ssh_public_key     = var.ssh_public_key
  vm_cores           = var.vm_cores
  vm_memory          = var.vm_memory
  vm_disk_size       = var.vm_disk_size
  preemptible        = var.preemptible
  vm_core_fraction   = var.vm_core_fraction

  # Cloud-init: передаём адрес Nexus для интеграции
  nexus_url = "http://${module.nexus.internal_ip}:8081"
  
  # Дополнительные пакеты для сервера
  docker_image       = var.teamcity_server_image
  container_name     = "teamcity-server"
  container_port     = 8111
  
  # Явная зависимость: сервер настраивается после Nexus
  depends_on         = [module.nexus]

  # Подключаем кастомный cloud-init для CentOS 7
  # cloud_init_custom  = "cloud-init-clickhouse.tpl"
}

module "teamcity_agent" {
  source = "./modules/vm"

  vm_name            = "teamcity-agent-vm"
  vm_hostname        = "teamcity-agent"
  vm_role            = "agent"

  project_label      = "teamcity"
  environment_label  = var.environment
  extra_labels       = { component = "build-executor" }

  subnet_id          = module.vpc.subnet_id
  security_group_ids = [module.security.security_group_id]
  
  zone               = var.default_zone
  image_family       = var.image_family_ubuntu

  ssh_public_key     = var.ssh_public_key
  vm_cores           = 2
  vm_memory          = var.vm_memory
  vm_disk_size       = 20
  preemptible        = var.preemptible
  vm_core_fraction   = var.vm_core_fraction
  
  # 🔑 КРИТИЧЕСКИ: передаём внутренний адрес сервера для авто-подключения
  # Используем internal_ip для надёжной связи внутри VPC
  server_url = "http://${module.teamcity_server.internal_ip}:8111"
  nexus_url  = "http://${module.nexus.internal_ip}:8081"
  
  docker_image       = var.teamcity_agent_image
  container_name     = "teamcity-agent"
  
  # Агент зависит от сервера: нельзя подключиться к несуществующему серверу
  depends_on = [module.teamcity_server]
}

