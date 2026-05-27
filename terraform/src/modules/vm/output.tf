# ======================================================================
# outputs.tf — Выходные данные модуля виртуальной машины
# ======================================================================

output "vm_id" {
  description = "ID виртуальной машины"
  value       = yandex_compute_instance.vm.id
}

output "vm_name" {
  description = "Имя виртуальной машины"
  value       = yandex_compute_instance.vm.name
}

output "vm_hostname" {
  description = "Hostname внутри ВМ"
  value       = yandex_compute_instance.vm.hostname
}

output "external_ip" {
  description = "Публичный IP-адрес ВМ"
  value       = yandex_compute_instance.vm.network_interface[0].nat_ip_address
}

output "internal_ip" {
  description = "Внутренний IP-адрес ВМ"
  value       = yandex_compute_instance.vm.network_interface[0].ip_address
}