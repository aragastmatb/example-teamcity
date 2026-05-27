## 📁 Структура проекта

```
terraform/
├── src/
│   ├── main.tf                    # Оркестрация модулей
│   ├── variables.tf               # Входные переменные с валидацией
│   ├── outputs.tf                 # Выходные данные для пользователя
│   ├── providers.tf               # Провайдеры и бэкенд
│   ├── inventory.tf               # Генерация Ansible inventory
│   ├── terraform.tfvars.example   # Шаблон переменных
│   └── modules/
│       ├── vpc/
│       │   ├── main.tf            # Создание сети и подсети
│       │   ├── variables.tf       # Переменные модуля VPC
│       │   └── outputs.tf         # Выходы модуля VPC
│       ├── security/
│       │   ├── main.tf            # Группа безопасности с динамическими правилами
│       │   ├── variables.tf       # Переменные модуля security
│       │   └── outputs.tf         # Выходы модуля security
│       └── vm/
│           ├── main.tf            # Универсальный модуль ВМ с cloud-init
│           ├── variables.tf       # Переменные модуля VM
│           ├── outputs.tf         # Выходы модуля VM
│           └── cloud-init-templates/
│               ├── server.tpl     # Cloud-init для TeamCity Server
│               ├── agent.tpl      # Cloud-init для TeamCity Agent
│               └── nexus.tpl      # Cloud-init для Nexus Repository
├── ansible/
│   ├── ansible.cfg                # Конфигурация Ansible
│   ├── inventory.yml              # Динамический инвентарь (генерируется Terraform)
│   ├── group_vars/
│   │   └── all.yml                # Глобальные переменные для всех хостов
│   ├── roles/
│   │   ├── teamcity-server/
│   │   │   ├── tasks/main.yml     # Задачи настройки сервера
│   │   │   ├── templates/
│   │   │   │   ├── teamcity-server.vmoptions.j2
│   │   │   │   └── maven-settings.xml.j2
│   │   │   └── handlers/main.yml  # Обработчики событий
│   │   ├── teamcity-agent/
│   │   │   ├── tasks/main.yml     # Задачи настройки агента
│   │   │   └── templates/
│   │   │       └── buildAgent.properties.j2
│   │   └── nexus/
│   │       ├── tasks/main.yml     # Установка и настройка Nexus
│   │       └── templates/
│   │           └── nexus.properties.j2
│   └── playbooks/
│       ├── site.yml               # Главный playbook
│       └── configure-all.yml      # Плейбук полной настройки
├── app/
│   ├── src/main/java/com/example/Welcomer.java      # Исходный код приложения
│   ├── src/test/java/com/example/WelcomerTest.java  # Тесты с проверкой фичи
│   ├── pom.xml                                       # Maven конфигурация с Nexus
│   └── teamcity/settings.xml                         # Maven settings для аутентификации
├── .teamcity/
│   ├── settings.kts             # Точка входа Kotlin DSL
│   ├── pom.xml                  # Maven для валидации DSL
│   └── project/
│       ├── Project.kt           # Описание проекта TeamCity
│       └── BuildType.kt         # Конфигурация сборки с условиями
├── README.md                    # Документация решения
└── .gitignore                   # Исключаемые файлы
```

## 🚀 Часть 4: Запуск и использование

- ```Инструкция по запуску```

```bash
# ============================================================================
# ПОЛНАЯ ИНСТРУКЦИЯ ПО ЗАПУСКУ ИНФРАСТРУКТУРЫ
# ============================================================================

# ----------------- 1. ПОДГОТОВКА ОКРУЖЕНИЯ -----------------

# Установите необходимые инструменты:
# - Terraform >= 1.6.0
# - Ansible >= 2.14  
# - Yandex Cloud CLI (yc)
# - Java 17, Maven 3.9+ (для локальной разработки)

# Аутентификация в Yandex Cloud:
yc config set token <ваш_токен>
yc config set cloud-id <cloud_id>
yc config set folder-id <folder_id>

# Экспорт переменных для Terraform:
export TF_VAR_service_account_key_file="~/.config/yandex-cloud/sa-key.json"
export TF_VAR_cloud_id="<cloud_id>"
export TF_VAR_folder_id="<folder_id>"
export TF_VAR_ssh_public_key="$(cat ~/.ssh/id_ed25519.pub)"

# ----------------- 2. НАСТРОЙКА ПЕРЕМЕННЫХ -----------------

# Перейдите в директорию terraform
cd terraform/

# Скопируйте пример переменных
cp terraform.tfvars.example terraform.tfvars

# Отредактируйте terraform.tfvars:
# - cloud_id, folder_id из yc config list
# - service_account_key_file путь к ключу
# - ssh_public_key содержимое вашего публичного ключа
# - allowed_ssh_cidr ваш IP для SSH (найдите на whatismyip.com)

# ----------------- 3. РАЗВЁРТЫВАНИЕ ИНФРАСТРУКТУРЫ -----------------

# Инициализация провайдеров и модулей
terraform init

# Предпросмотр изменений (ОБЯЗАТЕЛЬНО перед apply!)
terraform plan -out=tfplan

# Применение конфигурации (создание ВМ в Yandex Cloud)
terraform apply tfplan

# 🎉 После успешного apply:
# - Ansible запустится автоматически (если run_ansible=true)
# - Выведутся полезные URL и инструкции через terraform output

# ----------------- 4. ПЕРВОНАЧАЛЬНАЯ НАСТРОЙКА TEAMCITY -----------------

# 1. Откройте URL из вывода:
terraform output -raw teamcity_server | jq -r .web_url

# 2. Создайте пользователя-администратора в веб-интерфейсе
# 3. Введите лицензионный ключ или нажмите "Start Trial"
# 4. Перейдите в Administration → Agents
# 5. Нажмите Authorize для нового агента

# ----------------- 5. НАСТРОЙКА NEXUS -----------------

# 1. Откройте Nexus:
terraform output -raw nexus | jq -r .web_url

# 2. Логин: admin, Пароль: admin123 (смените при первом входе!)
# 3. Создайте пользователя для CI/CD:
#    - Username: ci-deployer
#    - Roles: nx-repository-view-maven2-*-read/add/edit
# 4. Добавьте параметры в TeamCity:
#    Project Settings → Parameters:
#      nexus.user = ci-deployer
#      nexus.password = ******** (тип: Password)
#      nexus.url = <внутренний_IP_из_terraform_output>

# ----------------- 6. ИМПОРТ ПРОЕКТА В TEAMCITY -----------------

# 1. Create Project → From URL
# 2. Укажите ссылку на ваш fork
# 3. Включите Versioned Settings → Format: Kotlin DSL
# 4. Нажмите Enable — конфигурация загрузится из .teamcity/

# ----------------- 7. ЗАПУСК СБОРКИ -----------------

# Локально:
mvn clean test          # Тесты
mvn clean deploy        # Деплой в Nexus (требует credentials)

# В TeamCity:
# - Push в ветку feature/add_reply → автозапуск тестов
# - Merge в master → автозапуск деплоя
# - Артефакты: вкладка "Artifacts" в сборке

# ----------------- 8. ПРОВЕРКА РЕЗУЛЬТАТА -----------------

# Проверьте, что артефакт появился в Nexus:
curl -u ci-deployer:password \
  http://<nexus_internal_ip>:8081/repository/maven-releases/com/example/example-teamcity/

# Проверьте артефакты в TeamCity:
# → Builds → выберите сборку → вкладка Artifacts
```