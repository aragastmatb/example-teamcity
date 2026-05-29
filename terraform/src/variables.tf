# ============================================================================
# ВХОДНЫЕ ПЕРЕМЕННЫЕ КОНФИГУРАЦИИ
# Все переменные документированы и имеют валидацию для предотвращения ошибок
# ============================================================================

# ----------------- АУТЕНТИФИКАЦИЯ YANDEX CLOUD -----------------

variable "service_account_key_file" {
  description = "Путь к файлу ключа сервисного аккаунта (JSON) для Terraform"
  type        = string
  sensitive   = true
  validation {
    condition     = endswith(var.service_account_key_file, ".json")
    error_message = "Файл ключа должен иметь расширение .json"
  }
}

variable "cloud_id" {
  description = "Идентификатор облака Yandex Cloud"
  type        = string
  validation {
    condition     = can(regex("^b1g[a-z0-9]+$", var.cloud_id))
    error_message = "cloud_id должен соответствовать формату Yandex Cloud (b1g...)."
  }
}

variable "folder_id" {
  description = "Идентификатор каталога для развёртывания"
  type        = string
  validation {
    condition     = can(regex("^b1g[a-z0-9]+$", var.folder_id))
    error_message = "folder_id должен соответствовать формату Yandex Cloud (b1g...)."
  }
}

# ----------------- СЕТЬ И ЗОНА ДОСТУПНОСТИ -----------------

variable "default_zone" {
  description = "Зона доступности по умолчанию"
  type        = string
  default     = "ru-central1-a"
  validation {
    condition     = contains(["ru-central1-a", "ru-central1-b", "ru-central1-d"], var.default_zone)
    error_message = "Допустимые зоны: ru-central1-a, ru-central1-b, ru-central1-d"
  }
}

variable "vpc_name" {
  description = "Имя сети VPC"
  type        = string
  default     = "network"
}

variable "subnet_name" {
  description = "Имя подсети"
  type        = string
  default     = "subnet"
}

variable "v4_cidr_blocks" {
  description = "CIDR-блок подсети"
  type        = list(string)
  default     = ["10.10.10.0/24"]
}

variable "trusted_cidrs" {
  description = "Доверенные CIDR для доступа к UI в prod"
  type        = list(string)
  default     = []
}

# ----------------- ОКРУЖЕНИЕ И БЕЗОПАСНОСТЬ -----------------

variable "environment" {
  description = "Окружение (prod/staging/dev)"
  type        = string
  default     = "dev"
}

variable "project_name" {
  description = "Имя проекта для тегирования ресурсов"
  type        = string
  default     = "teamcity-cicd"
}

variable "ssh_public_key" {
  description = "Публичный SSH-ключ"
  type        = string
  sensitive   = true
}

variable "allowed_ssh_cidr" {
  description = "CIDR для SSH (null — запретить)"
  type        = string
  default     = null
}

# ----------------- ОБРАЗЫ ВИРТУАЛЬНЫХ МАШИН -----------------

variable "image_family_ubuntu" {
  description = "Семейство образа Ubuntu"
  type        = string
  default     = "ubuntu-2204-lts"
}

variable "image_family_rhel" {
  description = "Семейство образа RHEL"
  type        = string
  default     = "almalinux-9"
}

variable "image_family_debian" {
  description = "Семейство образа Debian"
  type        = string
  default     = "debian-12"
}

# ----------------- РЕСУРСЫ ВИРТУАЛЬНЫХ МАШИН -----------------

variable "vm_cores" {
  description = "Количество vCPU"
  type        = number
  default     = 4
}

variable "vm_core_fraction" {
  description = "Гарантированная доля vCPU (5, 20, 50, 100)"
  type        = number
  default     = 20
}

variable "vm_memory" {
  description = "Объём ОЗУ (ГБ)"
  type        = number
  default     = 4
}

variable "vm_disk_size" {
  description = "Размер диска (ГБ)"
  type        = number
  default     = 30
}

variable "preemptible" {
  description = "Использовать прерываемую ВМ"
  type        = bool
  default     = true
}

variable "vm_platform_id" {
  description = "Платформа ВМ"
  type        = string
  default     = "standard-v3"
}

variable "server_url" {
  description = "Конфигурация VM для TeamCity Agent"
  type        = string
}

# ----------------- ANSIBLE ИНТЕГРАЦИЯ -----------------

variable "run_ansible" {
  description = "Запускать ли Ansible-плейбуки автоматически после terraform apply"
  type        = bool
  default     = true
}

variable "ansible_inventory_path" {
  description = "Путь к файлу Ansible inventory (генерируется Terraform)"
  type        = string
  default     = "../../ansible/inventory/inventory.yml"
}

variable "ansible_ssh_key_path" {
  description = "Путь к приватному SSH-ключу для Ansible"
  type        = string
  default     = "~/.ssh/id_ed25519"
}

variable "ssh_user" {
  description = "Имя пользователя для SSH-подключения к ВМ"
  type        = string
  default     = "yc-user"
}

# ----------------- NEXUS НАСТРОЙКИ -----------------

variable "nexus_admin_password" {
  description = "Пароль администратора Nexus (оставьте пустым для использования дефолтного)"
  type        = string
  default     = ""
  sensitive   = true  # Не показывать в выводе
}

# ----------------- ПАКЕТЫ И CLOUD-INIT -----------------

variable "base_packages" {
  description = "Список базовых пакетов для установки на все виртуальные машины"
  type        = list(string)
  default     = ["curl", "wget", "git", "unzip", "jq", "htop", "tree"]
}

# ==================== DOCKER ОБРАЗЫ ====================

variable "teamcity_server_image" {
  description = "Docker образ TeamCity Server (Docker Hub)"
  type        = string
  default     = "jetbrains/teamcity-server:latest"
}

variable "teamcity_agent_image" {
  description = "Docker образ TeamCity Agent (Docker Hub)"
  type        = string
  default     = "jetbrains/teamcity-agent:latest"
}

variable "nexus_image" {
  description = "Docker образ Nexus Repository (Docker Hub)"
  type        = string
  default     = "sonatype/nexus3:latest"
}