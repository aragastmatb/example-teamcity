# ============================================================================
# ВЫХОДНЫЕ ДАННЫЕ МОДУЛЯ SECURITY
# ============================================================================

output "security_group_id" {
  description = "Уникальный идентификатор созданной группы безопасности"
  value       = yandex_vpc_security_group.sg.id
}

output "security_group_name" {
  description = "Имя созданной группы безопасности"
  value       = yandex_vpc_security_group.sg.name
}