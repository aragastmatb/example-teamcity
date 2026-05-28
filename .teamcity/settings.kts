import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.projectFeatures.buildReportTab

version = "2026.1"

project {
    description = "TeamCity CI/CD Homework"
    
    // Подключаем корневой VCS и подпроект
    vcsRoot(DslContext.settingsRoot)
    subProject(Project)

    // Параметры наследуются всеми сборками проекта
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