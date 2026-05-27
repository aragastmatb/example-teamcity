# ============================================================================
# МОДУЛЬ: VPC (Virtual Private Cloud)
# Создаёт изолированную сеть и подсеть для инфраструктуры
# ============================================================================

# ----------------- СОЗДАНИЕ СЕТИ -----------------
resource "yandex_vpc_network" "network" {
  # Имя сети из переменной модуля
  name = var.network_name

  # Метки для управления, биллинга и автоматизации
  labels = {
    # Окружение для фильтрации ресурсов
    environment = var.environment
    # Указание, что ресурс управляется Terraform
    managed_by  = "terraform"
    # Имя проекта для группировки
    project     = var.project_name
  }
  
  # Описание для удобства в консоли Yandex Cloud
  description = "Изолированная сеть для CI/CD инфраструктуры"
}

# ----------------- СОЗДАНИЕ ПОДСЕТИ -----------------
resource "yandex_vpc_subnet" "subnet" {
  # Имя подсети из переменной
  name = var.subnet_name
  
  # Привязка к зоне доступности
  zone = var.zone
  
  # Ссылка на созданную сеть
  network_id = yandex_vpc_network.network.id
  
  # CIDR-блоки для выделения адресов ВМ внутри подсети
  v4_cidr_blocks = var.v4_cidr_blocks
  
  # Метки для управления
  labels = {
    environment = var.environment
    managed_by  = "terraform"
  }
  
  # Описание подсети
  description = "Подсеть для размещения компонентов CI/CD пайплайна"
}