# TeamCity Configuration Guide

## Структура файлов
teamcittty/
├── .teamcity/
│ ├── settings.kts # Главная конфигурация проекта
│ └── pom.kts # Конфигурации сборок Build и Deploy
├── pom.xml # Maven конфигурация
└── src/ # Исходный код

text

## Настройка в TeamCity

### 1. Создать проект
1. В TeamCity: Create Project
2. Имя: `teamcittty`
3. Выбрать "From repository URL"
4. Указать: `https://github.com/sapr797/teamcittty.git`

### 2. Настроить VCS Root
TeamCity автоматически создаст VCS root с именем "Team"

### 3. Включить Kotlin DSL
1. Проект → Edit Project Settings
2. Versioned Settings
3. Включить синхронизацию
4. Format: Kotlin
5. Path: `.teamcity`
6. Нажать "Apply"

### 4. Создать сборки
TeamCity автоматически создаст сборки Build и Deploy из Kotlin DSL

## Проблемы и решения

### Ошибка: "Failed to resolve artifacts"
1. Убедитесь, что сборка "Build" существует
2. Запустите сборку "Build" и дождитесь успешного завершения
3. Проверьте, что в Build есть артефакты (вкладка Artifacts)

### Ошибка: "teamcity-ivy.xml 404"
В файле `.teamcity/pom.kts` в разделе dependencies добавьте:
```kotlin
useIvyFormat = false
Параметр buildRule
Параметр buildRule = lastSuccessful() находится в:

Файл: .teamcity/pom.kts

Раздел: dependencies { artifacts(...) { ... } }

Строка: внутри блока зависимостей Deploy от Build
