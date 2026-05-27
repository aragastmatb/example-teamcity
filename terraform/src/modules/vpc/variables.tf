# ============================================================================
# ПЕРЕМЕННЫЕ МОДУЛЯ VPC
# ============================================================================

variable "network_name" {
  description = "Имя сети"
  type        = string
}

variable "subnet_name" {
  description = "Имя подсети"
  type        = string
}

variable "zone" {
  description = "Зона доступности"
  type        = string
}

variable "v4_cidr_blocks" {
  description = "Список CIDR-блоков подсети"
  type        = list(string)
}

variable "environment" {
  description = "Окружение (prod/staging/dev)"
  type        = string
  default = "dev"
}

variable "project_name" {
  description = "Имя проекта для тегирования ресурсов VPC"
  type        = string
  default     = "teamcity-cicd"
}