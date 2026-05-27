# Terraform Module: vm

---

## 📌 Описание
Модуль инфраструктуры: **vm**

---

## 📚 Документация
<!-- BEGIN_TF_DOCS -->
## Requirements

| Name | Version |
|------|---------|
| <a name="requirement_terraform"></a> [terraform](#requirement_terraform) | >= 1.12.0 |
| <a name="requirement_yandex"></a> [yandex](#requirement_yandex) | >= 0.120 |

## Providers

| Name | Version |
|------|---------|
| <a name="provider_yandex"></a> [yandex](#provider_yandex) | >= 0.120 |

## Resources

| Name | Type |
|------|------|
| [yandex_compute_instance.vm](https://registry.terraform.io/providers/yandex-cloud/yandex/latest/docs/resources/compute_instance) | resource |

## Inputs

| Name | Description | Type | Default | Required |
|------|-------------|------|---------|:--------:|
| <a name="input_cloud_init_custom"></a> [cloud_init_custom](#input_cloud_init_custom) | Путь к кастомному шаблону cloud‑init | `string` | `null` | no |
| <a name="input_environment_label"></a> [environment_label](#input_environment_label) | Метка окружения | `string` | n/a | yes |
| <a name="input_image_family"></a> [image_family](#input_image_family) | Семейство образа ОС | `string` | n/a | yes |
| <a name="input_package_update"></a> [package_update](#input_package_update) | Обновлять ли список пакетов перед установкой | `bool` | `true` | no |
| <a name="input_package_upgrade"></a> [package_upgrade](#input_package_upgrade) | Выполнять ли полное обновление системы | `bool` | `false` | no |
| <a name="input_packages"></a> [packages](#input_packages) | Список пакетов для установки | `list(string)` | <pre>[<br/>  "python3",<br/>  "curl",<br/>  "wget",<br/>  "tar",<br/>  "gzip",<br/>  "git",<br/>  "ca-certificates",<br/>  "gnupg"<br/>]</pre> | no |
| <a name="input_preemptible"></a> [preemptible](#input_preemptible) | Использовать прерываемую ВМ | `bool` | n/a | yes |
| <a name="input_project_label"></a> [project_label](#input_project_label) | Метка проекта | `string` | n/a | yes |
| <a name="input_security_group_ids"></a> [security_group_ids](#input_security_group_ids) | Список ID групп безопасности | `list(string)` | n/a | yes |
| <a name="input_ssh_public_key"></a> [ssh_public_key](#input_ssh_public_key) | Публичный SSH-ключ | `string` | n/a | yes |
| <a name="input_subnet_id"></a> [subnet_id](#input_subnet_id) | ID подсети для подключения ВМ | `string` | n/a | yes |
| <a name="input_timezone"></a> [timezone](#input_timezone) | Часовой пояс | `string` | `"Europe/Moscow"` | no |
| <a name="input_user_name"></a> [user_name](#input_user_name) | Имя пользователя по умолчанию | `string` | `"yc-user"` | no |
| <a name="input_user_shell"></a> [user_shell](#input_user_shell) | Оболочка пользователя | `string` | `"/bin/bash"` | no |
| <a name="input_user_sudo"></a> [user_sudo](#input_user_sudo) | Права sudo для пользователя | `string` | `"ALL=(ALL) NOPASSWD:ALL"` | no |
| <a name="input_vm_core_fraction"></a> [vm_core_fraction](#input_vm_core_fraction) | Гарантированная доля vCPU | `number` | `20` | no |
| <a name="input_vm_cores"></a> [vm_cores](#input_vm_cores) | Количество vCPU | `number` | n/a | yes |
| <a name="input_vm_disk_size"></a> [vm_disk_size](#input_vm_disk_size) | Размер загрузочного диска (ГБ) | `number` | n/a | yes |
| <a name="input_vm_hostname"></a> [vm_hostname](#input_vm_hostname) | Hostname внутри ВМ | `string` | n/a | yes |
| <a name="input_vm_memory"></a> [vm_memory](#input_vm_memory) | Объём оперативной памяти (ГБ) | `number` | n/a | yes |
| <a name="input_vm_name"></a> [vm_name](#input_vm_name) | Имя виртуальной машины | `string` | n/a | yes |
| <a name="input_vm_platform_id"></a> [vm_platform_id](#input_vm_platform_id) | Платформа ВМ | `string` | `"standard-v3"` | no |
| <a name="input_zone"></a> [zone](#input_zone) | Зона доступности | `string` | n/a | yes |

## Outputs

| Name | Description |
|------|-------------|
| <a name="output_external_ip"></a> [external_ip](#output_external_ip) | Публичный IP-адрес ВМ |
| <a name="output_internal_ip"></a> [internal_ip](#output_internal_ip) | Внутренний IP-адрес ВМ |
| <a name="output_vm_hostname"></a> [vm_hostname](#output_vm_hostname) | Hostname внутри ВМ |
| <a name="output_vm_id"></a> [vm_id](#output_vm_id) | ID виртуальной машины |
| <a name="output_vm_name"></a> [vm_name](#output_vm_name) | Имя виртуальной машины |
<!-- END_TF_DOCS -->
