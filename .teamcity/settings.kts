// .teamcity/settings.kts
// Указываем версию TeamCity (должна соответствовать серверу)
version = "2025.11"

// Определяем проект
project {
    // Регистрируем все конфигурации сборок в проекте
    // Здесь перечисляем ВСЕ наши build types
    buildType(Build)
    buildType(Deploy)
    // buildType(Test) - если нужно
}
