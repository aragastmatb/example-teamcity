# ============================================================================
# МОДУЛЬ: SECURITY GROUP
# Создаёт группу безопасности с гибкими правилами доступа
# Использует dynamic блоки для условного создания правил
# ============================================================================

resource "yandex_vpc_security_group" "sg" {
  # Имя и описание из переменных
  name        = var.name
  description = var.description
  
  # Привязка к сети
  network_id = var.network_id
  
  # ========================================================================
  # ВХОДЯЩИЕ ПРАВИЛА (INGRESS) — что разрешено ДОПУСКАТЬ к ресурсам
  # ========================================================================
  
  # ----------------- SSH ДОСТУП (условный) -----------------
  # Создаётся только если задан allowed_ssh_cidr
  dynamic "ingress" {
    # for_each создаёт 1 элемент если условие истинно, иначе 0
    for_each = var.allowed_ssh_cidr != null && var.allowed_ssh_cidr != "" ? [1] : []
    
    content {
      description    = "SSH access for administration"
      protocol       = "TCP"
      port           = 22
      # Разрешаем доступ только с указанного диапазона адресов
      v4_cidr_blocks = [var.allowed_ssh_cidr]
    }
  }
  
  # ----------------- TEAMCITY WEB UI -----------------
  dynamic "ingress" {
    for_each = var.enable_teamcity_ui ? [1] : []
    content {
      description    = "TeamCity Web Interface (port 8111)"
      protocol       = "TCP"
      port           = 8111
      # В production ограничьте teamcity_ui_cidrs вашим офисным IP!
      v4_cidr_blocks = var.teamcity_ui_cidrs
    }
  }
  
  # ----------------- NEXUS WEB UI -----------------
  dynamic "ingress" {
    for_each = var.enable_nexus_ui ? [1] : []
    content {
      description    = "Nexus Repository Web UI (port 8081)"
      protocol       = "TCP"
      port           = 8081
      v4_cidr_blocks = var.nexus_ui_cidrs
    }
  }
  
  # ----------------- ВНУТРЕННЯЯ КОММУНИКАЦИЯ -----------------
  # Разрешаем связь между компонентами внутри подсети
  # Диапазон портов 8000-9000 покрывает:
  # - 8111: TeamCity Server
  # - 9090: TeamCity Agent listener
  # - 8081: Nexus Repository
  ingress {
    description    = "Internal CI/CD components communication"
    protocol       = "TCP"
    from_port      = var.internal_port_range_start
    to_port        = var.internal_port_range_end
    # Разрешаем только из подсетей приложения
    v4_cidr_blocks = var.app_subnet_cidrs
  }
  
  # ========================================================================
  # ИСХОДЯЩИЕ ПРАВИЛА (EGRESS) — что разрешено ОТПРАВЛЯТЬ из ресурсов
  # ========================================================================
  
  # Разрешаем весь исходящий трафик
  # Необходимо для:
  # - обновлений пакетов (apt/yum)
  # - скачивания зависимостей Maven
  # - git clone/pull
  # - отправки уведомлений (Slack, email)
  egress {
    description    = "Allow all outbound traffic for updates and dependencies"
    protocol       = "ANY"  # Любой протокол (TCP, UDP, ICMP)
    v4_cidr_blocks = ["0.0.0.0/0"]  # Любой адрес назначения
  }
  
  # ----------------- МЕТАДАННЫЕ И ТЕГИ -----------------
  labels = merge(
    {
      # Стандартные метки для управления
      managed_by  = "terraform"
      environment = var.environment
    },
    # Дополнительные пользовательские метки
    var.extra_labels
  )
}