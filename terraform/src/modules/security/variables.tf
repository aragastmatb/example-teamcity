# ============================================================================
# ПЕРЕМЕННЫЕ МОДУЛЯ SECURITY
# ============================================================================

variable "name" {
  description = "Имя группы безопасности"
  type        = string
}

variable "description" {
  description = "Описание группы безопасности для консоли"
  type        = string
}

variable "network_id" {
  description = "ID сети VPC для привязки группы безопасности"
  type        = string
}

variable "environment" {
  description = "Окружение для тегирования"
  type        = string
}

variable "allowed_ssh_cidr" {
  description = "CIDR для SSH доступа (null = запретить)"
  type        = string
  default     = null
}

variable "enable_teamcity_ui" {
  description = "Открыть ли порт 8111 для TeamCity Web UI"
  type        = bool
  default     = true
}

variable "teamcity_ui_cidrs" {
  description = "CIDR-блоки для доступа к TeamCity UI"
  type        = list(string)
  default     = ["0.0.0.0/0"]  # ⚠️ Ограничьте в production!
}

variable "enable_nexus_ui" {
  description = "Открыть ли порт 8081 для Nexus Web UI"
  type        = bool
  default     = true
}

variable "nexus_ui_cidrs" {
  description = "CIDR-блоки для доступа к Nexus UI"
  type        = list(string)
  default     = ["0.0.0.0/0"]
}

variable "app_subnet_cidrs" {
  description = "CIDR-блоки подсети для внутренней коммуникации"
  type        = list(string)
}

variable "internal_port_range_start" {
  description = "Начало диапазона внутренних портов"
  type        = number
  default     = 8000
}

variable "internal_port_range_end" {
  description = "Конец диапазона внутренних портов"
  type        = number
  default     = 9000
}

variable "extra_labels" {
  description = "Дополнительные метки для группы безопасности"
  type        = map(string)
  default     = {}
}