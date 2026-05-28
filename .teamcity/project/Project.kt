import jetbrains.buildServer.configs.kotlin.*

/*
 * Описывает структуру проекта TeamCity:
 * - Идентификатор и отображаемое имя
 * - Вложенные BuildType (конфигурации сборок)
 * - Общие настройки для всех сборок проекта
 */

object Project : Project({
    // Уникальный идентификатор проекта (используется в API и URL)
    id = "ExampleTeamcityProject"
    
    // Отображаемое имя в интерфейсе TeamCity
    name = "Example TeamCity Project"
    
    // Подключаем основную конфигурацию сборки
    buildType(Build)
    
    // Общие настройки VCS для всех сборок проекта
    vcsRoot {
        // Используем корневой VCS из контекста
        id = DslContext.settingsRoot.id
        // Проверяем все ветки, а не только master
        branchFilter = """
            +:*
            -:pull/*
        """.trimIndent()
    }
    
    // Настройки автоматического запуска сборок
    features {
        // Включает мониторинг веток для автоматического запуска сборок
        feature {
            type = "feature-branches"
            param("policy", "ALL")  // Строить все ветки
        }
    }
})