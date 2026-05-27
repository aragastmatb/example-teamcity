# ============================================================================
# МОДУЛЬ: VM (Virtual Machine)
# Универсальный модуль для создания ВМ с гибкой настройкой через cloud-init
# Поддерживает разные роли: server, agent, nexus, generic
# ============================================================================

# ----------------- ПОЛУЧЕНИЕ ИНФОРМАЦИИ ОБ ОБРАЗЕ -----------------
# data source запрашивает актуальный ID образа по имени семейства
# Это обеспечивает использование последней версии образа при каждом apply
data "yandex_compute_image" "os_image" {
  family = var.image_family
}

# ----------------- ЛОКАЛЬНЫЕ ПЕРЕМЕННЫЕ ДЛЯ CLOUD-INIT -----------------
locals {
  # Базовые переменные, доступные в любом cloud-init шаблоне
  base_cloud_init_vars = {
    # Базовые
    vm_hostname     = var.vm_hostname
    ssh_public_key  = var.ssh_public_key
    timezone        = var.timezone
    user_name       = var.user_name
    user_sudo       = var.user_sudo
    user_shell      = var.user_shell
    packages        = jsonencode(var.install_packages)
    package_update  = var.package_update
    package_upgrade = var.package_upgrade
    vm_role         = var.vm_role

    # Docker-специфичные
    docker_image       = var.docker_image
    container_name     = var.container_name
    container_port     = var.container_port
    container_env      = jsonencode(var.container_env)
    
    # Ролевые
    server_url         = var.server_url
    nexus_url          = var.nexus_url
    nexus_admin_password = var.nexus_admin_password
    nexus_version   = var.nexus_version
  }

  # Выбор шаблона: кастомный или стандартный
  # cloud_init_template = var.cloud_init_custom != null ? var.cloud_init_custom : "${path.module}/cloud-init.tpl"

  # Генерация содержимого cloud-init
  #cloud_init_content = templatefile(local.cloud_init_template, local.cloud_init_vars)

  # Переменные, специфичные для каждой роли
  role_specific_vars = {
    # TeamCity Server: нужен доступ к Nexus для деплоя
    # Сервер не подключается к другому серверу
    server = {
      server_url = ""
      nexus_url  = var.nexus_url
    }
    # TeamCity Agent: должен знать URL сервера для подключения
    agent = {
      server_url = var.server_url
      nexus_url  = var.nexus_url
    }
    # Nexus Repository: требует начальный пароль админа
    nexus = {
      server_url = ""
      nexus_url  = ""
      # Используем дефолтный пароль если не задан
      nexus_admin_password = var.nexus_admin_password != "" ? var.nexus_admin_password : "admin123"
    }
    # Generic: пустой набор для произвольных ВМ
    generic = {}
  }

  # Объединяем все переменные в один map для templatefile()
  # lookup возвращает пустой map если роль не найдена (защита от ошибок)
  cloud_init_vars = merge(
    local.base_cloud_init_vars,
    lookup(local.role_specific_vars, var.vm_role, {}),
    var.cloud_init_vars  # Пользовательские переменные переопределяют стандартные
  )

  # Выбор шаблона cloud-init по приоритету:
  # 1. Явно заданный кастомный шаблон
  # 2. Шаблон по умолчанию для роли в cloud-init-templates/
  cloud_init_template = var.cloud_init_custom != null ? var.cloud_init_custom : "${path.module}/cloud-init-templates/${var.vm_role}.tpl"

  # Генерация финального контента cloud-init
  # templatefile() подставляет переменные и возвращает строку
  cloud_init_content = templatefile(local.cloud_init_template, local.cloud_init_vars)
}

# ----------------- РЕСУРС: ВИРТУАЛЬНАЯ МАШИНА -----------------
resource "yandex_compute_instance" "vm" {
  name        = var.vm_name
  hostname    = var.vm_hostname
  platform_id = var.vm_platform_id
  zone        = var.zone

  # Ресурсы вычислений
  resources {
    cores         = var.vm_cores
    memory        = var.vm_memory
    core_fraction = var.vm_core_fraction
  }

  # Загрузочный диск
  boot_disk {
    initialize_params {
      image_id = data.yandex_compute_image.os_image.image_id
      size     = var.vm_disk_size
      type     = var.disk_type
      name     = "disk-${var.vm_name}"
    }
  }

  # Сетевой интерфейс
  network_interface {
    subnet_id          = var.subnet_id
    nat                = true
    security_group_ids = var.security_group_ids
  }

  # Метаданные: cloud-init + отладка
  metadata = {
    user-data              = local.cloud_init_content
    serial-port-enable     = 1
    teamcity-role          = var.vm_role
    ssh-keys               = "${var.user_name}:${var.ssh_public_key}"
  }

  # Политика планирования
  scheduling_policy {
    preemptible = var.preemptible
  }

  # Метки для управления и биллинга
  labels = {
    project     = var.project_label
    environment = var.environment_label
    managed_by  = "terraform"
    role        = var.vm_role
  }

  # Таймауты для долгих операций
  timeouts {
    create = "20m"
    update = "20m"
    delete = "10m"
  }
}