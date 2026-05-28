import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.projectFeatures.*

// ============================================================================
// ГЛОБАЛЬНЫЕ НАСТРОЙКИ ПРОЕКТА
// ============================================================================

/*
 * Это основной файл, который TeamCity загружает при включении Versioned Settings.
 * Он определяет:
 * - Версию DSL (должна соответствовать версии TeamCity)
 * - Ссылку на VCS-репозиторий
 * - Корневой проект и его вложенные конфигурации
 */

version = "2026.1"  // Версия TeamCity, для которой генерируется конфигурация

project {
    // Связываем проект с текущим репозиторием (автоматически подставляется DslContext)
    vcsRoot(DslContext.settingsRoot)
    
    // Подключаем описание проекта и сборок из отдельных файлов
    subProject(Project)
    
    // Глобальные параметры для всех сборок
    params {
        param("nexus.url", "10.10.10.25:8081")       // Замените на внутренний IP Nexus
        param("nexus.user", "ci-deployer")
        password("nexus.password", "zxx68ea4a703e0d8702b4a878ac238ec1a7")
    }

    features {
        buildReportTab {
            id = "PROJECT_EXT_1"
            title = "Code Coverage"
            startPage = "coverage.zip!index.html"
        }
    }

    cleanup {
        baseRule {
            preventDependencyCleanup = false
        }
    }
}