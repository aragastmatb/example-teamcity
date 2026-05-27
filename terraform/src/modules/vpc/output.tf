# ============================================================================
# ВЫХОДНЫЕ ДАННЫЕ МОДУЛЯ VPC
# Эти значения доступны в родительском модуле через module.vpc.<output_name>
# ============================================================================

output "network_id" {
  description = "Уникальный идентификатор созданной сети"
  value       = yandex_vpc_network.network.id
}

output "network_name" {
  description = "Имя созданной сети"
  value       = yandex_vpc_network.network.name
}

output "subnet_id" {
  description = "Уникальный идентификатор созданной подсети"
  value       = yandex_vpc_subnet.subnet.id
}

output "subnet_name" {
  description = "Имя созданной подсети"
  value       = yandex_vpc_subnet.subnet.name
}

output "subnet_cidr" {
  description = "CIDR-блок подсети для использования в правилах безопасности"
  value       = var.v4_cidr_blocks
}