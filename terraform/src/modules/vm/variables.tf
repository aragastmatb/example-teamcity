# ============================================================================
# ПЕРЕМЕННЫЕ МОДУЛЯ VM
# Все переменные, используемые в modules/vm/main.tf
# ============================================================================

variable "vm_name" {
  description = "Имя виртуальной машины"
  type        = string
}

variable "vm_hostname" {
  description = "Hostname внутри ВМ"
  type        = string
}

variable "project_label" {
  description = "Метка проекта"
  type        = string
  default     = "teamcity"
}

variable "environment_label" {
  description = "Метка окружения"
  type        = string
  default     = "dev"
}

variable "extra_labels" {
  description = "Дополнительные пользовательские метки"
  type        = map(string)
  default     = {}
}

variable "subnet_id" {
  description = "ID подсети для подключения ВМ"
  type        = string
}

variable "security_group_ids" {
  description = "Список ID групп безопасности"
  type        = list(string)
}

variable "zone" {
  description = "Зона доступности"
  type        = string
}

variable "image_family" {
  description = "Семейство образа ОС"
  type        = string
}

variable "ssh_public_key" {
  description = "Публичный SSH-ключ"
  type        = string
}

variable "vm_cores" {
  description = "Количество vCPU"
  type        = number
}

variable "vm_core_fraction" {
  description = "Гарантированная доля vCPU (5, 20, 50, 100)"
  type        = number
  default     = 20
}

variable "vm_memory" {
  description = "Объём оперативной памяти (ГБ)"
  type        = number
}

variable "vm_disk_size" {
  description = "Размер загрузочного диска (ГБ)"
  type        = number
}

variable "disk_type" {
  description = "Тип диска: network-hdd или network-ssd"
  type        = string
  default     = "network-hdd"
  validation {
    condition     = contains(["network-hdd", "network-ssd"], var.disk_type)
    error_message = "disk_type должен быть network-hdd или network-ssd"
  }
}

variable "preemptible" {
  description = "Использовать прерываемую ВМ"
  type        = bool
}

variable "vm_platform_id" {
  description = "Платформа ВМ"
  type        = string
  default     = "standard-v3"
}

# ==================== CLOUD-INIT СПЕЦИФИЧНЫЕ ПЕРЕМЕННЫЕ ====================

variable "timezone" {
  description = "Часовой пояс"
  type        = string
  default     = "Europe/Moscow"
}

variable "user_name" {
  description = "Имя пользователя по умолчанию"
  type        = string
  default     = "yc-user"
}

variable "user_sudo" {
  description = "Права sudo для пользователя"
  type        = string
  default     = "ALL=(ALL) NOPASSWD:ALL"
}

variable "user_shell" {
  description = "Оболочка пользователя"
  type        = string
  default     = "/bin/bash"
}

variable "packages" {
  description = "Список пакетов для установки"
  type        = list(string)
  default     = ["python3", "curl", "wget", "tar", "gzip", "git", "ca-certificates", "gnupg"]
}

variable "package_update" {
  description = "Обновлять ли список пакетов перед установкой"
  type        = bool
  default     = true
}

variable "package_upgrade" {
  description = "Выполнять ли полное обновление системы"
  type        = bool
  default     = false
}

variable "cloud_init_custom" {
  description = "Путь к кастомному шаблону cloud-init (если не задан — используется стандартный)"
  type        = string
  default     = null
}

variable "cloud_init_vars" {
  description = "Дополнительные переменные для подстановки в cloud-init шаблон"
  type        = map(string)
  default     = {}
}

variable "install_packages" {
  description = "Список пакетов для установки через cloud-init"
  type        = list(string)
  default     = ["python3", "curl", "wget", "tar", "gzip", "git", "ca-certificates", "gnupg", "unzip", "jq"]
}

variable "run_commands" {
  description = "Список команд для выполнения после установки пакетов (cloud-init runcmd)"
  type        = list(string)
  default     = []
}

variable "write_files" {
  description = "Файлы для создания через cloud-init (формат: {path, content, permissions})"
  type = list(object({
    path        = string
    content     = string
    permissions = optional(string, "0644")
    owner       = optional(string, "root:root")
  }))
  default = []
}

# ==================== CLOUD-INIT СПЕЦИФИЧНЫЕ ПЕРЕМЕННЫЕ ====================

variable "docker_image" {
  description = "Docker образ для запуска"
  type = string
  default = ""
}

variable "container_name" {
  description = "Имя контейнера"
  type = string
  default = ""
}

variable "container_port" {
  description = "Порт контейнера"
  type = number
  default = 80
}

variable "container_env" {
  description = "Переменные окружения для контейнера (map)"
  type = map(string)
  default = {}
}

# ==================== СПЕЦИФИЧНЫЕ ДЛЯ РОЛЕЙ ПЕРЕМЕННЫЕ ====================

variable "server_url" {
  description = "URL TeamCity Server для подключения агента (только для role=agent)"
  type        = string
  default     = ""
}

variable "nexus_url" {
  description = "URL Nexus Repository для Maven-интеграции"
  type        = string
  default     = ""
}

variable "vm_role" {
  description = "Роль ВМ: server/agent/nexus"
  type        = string
  validation {
    condition     = contains(["server", "agent", "nexus", "generic"], var.vm_role)
    error_message = "vm_role должен быть одним из: server, agent, nexus, generic"
  }
}

variable "nexus_admin_password" {
  description = "Пароль администратора Nexus (используется только при vm_role = \"nexus\")"
  type        = string
  default     = ""
  sensitive   = true  # Не выводить значение в логах и terraform output
}

variable "nexus_version" {
  description = "Версия Nexus Repository Manager для установки"
  type        = string
  default     = "3.61.0-02"
}