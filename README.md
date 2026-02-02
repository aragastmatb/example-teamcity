# TeamCity CI/CD Конфигурация

Проект содержит настройки TeamCity для автоматизированной сборки и деплоя Maven проекта.

## **Задание 4: Условная сборка**

Реализована условная сборка в зависимости от ветки:

### **Логика:**
- **master ветка**: `mvn clean deploy` (деплой в Nexus)
- **другие ветки**: `mvn clean test` (только запуск тестов)

### **Реализация:**
Конфигурация в `.teamcity/pom.kts` содержит два шага с условиями:

```kotlin
// Для master ветки
maven {
    name = "Deploy on master branch"
    conditions {
        or {
            equals("teamcity.build.branch", "master")
            equals("teamcity.build.branch", "refs/heads/master")
            matches("teamcity.build.branch", "")
        }
    }
    goals = "clean deploy"
    mavenSettings = id("Nexus_Settings")
}

// Для других веток
maven {
    name = "Test on other branches"
    conditions {
        and {
            isNotEmpty("teamcity.build.branch")
            doesNotEqual("teamcity.build.branch", "master")
            doesNotEqual("teamcity.build.branch", "refs/heads/master")
        }
    }
    goals = "clean test"
    mavenSettings = id("Nexus_Settings")
}
Задание 5: Настройка подключения к Nexus
Для подключения к Nexus создан файл settings.xml с учетными данными.

Содержимое settings.xml:
xml
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.2.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.2.0 
          http://maven.apache.org/xsd/settings-1.2.0.xsd">
    <servers>
        <server>
            <id>nexus-releases</id>
            <username>admin</username>
            <password>admin123</password>
        </server>
        <server>
            <id>nexus-snapshots</id>
            <username>admin</username>
            <password>admin123</password>
        </server>
    </servers>
</settings>
Интеграция в TeamCity:
Файл загружен в TeamCity в раздел "Maven Settings"

Имя конфигурации: Nexus_Settings

В конфигурации сборки добавлена ссылка на настройки

Задание 6: Обновление ссылок в pom.xml
Обновлены URL репозиториев в pom.xml:

Исправления:
distributionManagement - URL для релизов и снапшотов

repositories - URL для загрузки зависимостей

pluginRepositories - URL для загрузки плагинов

Пример исправленных URL:
xml
<distributionManagement>
    <repository>
        <id>nexus-releases</id>
        <url>http://localhost:8081/repository/maven-releases/</url>
    </repository>
    <snapshotRepository>
        <id>nexus-snapshots</id>
        <url>http://localhost:8081/repository/maven-snapshots/</url>
    </snapshotRepository>
</distributionManagement>

<repositories>
    <repository>
        <id>nexus</id>
        <url>http://localhost:8081/repository/maven-public/</url>
        <releases><enabled>true</enabled></releases>
        <snapshots><enabled>true</enabled></snapshots>
    </repository>
</repositories>
Настройка для вашего Nexus:
Замените localhost:8081 на адрес вашего Nexus сервера.

Структура проекта
text
teamcittty/
├── .teamcity/               # Конфигурация TeamCity Kotlin DSL
│   ├── settings.kts        # Главная конфигурация проекта
│   └── pom.kts             # Конфигурации сборок Build и Deploy
├── pom.xml                 # Maven конфигурация с обновленными URL
├── settings.xml            # Учетные данные Nexus
├── src/                    # Исходный код
└── README.md               # Документация
Конфигурация TeamCity
Проекты:
Build - основная сборка с условной логикой

Deploy - развертывание с зависимостью от артефактов Build

Артефакты:
JAR файлы публикуются в TeamCity: target/*.jar => artifacts/

Deploy сборка скачивает артефакты в директорию build-output/

Как развернуть
Создать проект TeamCity из репозитория

Включить Kotlin DSL синхронизацию (путь: .teamcity)

Загрузить settings.xml в TeamCity (Администрирование → Maven Settings)

Запустить сборку Build на ветке master

Примечания
Nexus должен быть доступен по указанному в pom.xml адресу

Учетные данные по умолчанию: admin/admin123

Для работы деплоя необходимо запустить Nexus сервер
