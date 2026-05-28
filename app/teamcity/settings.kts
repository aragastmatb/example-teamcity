// ============================================================================
// TEAMCITY KOTLIN DSL: Конфигурация проекта и сборки
// Документация: <teamcity>/app/dsl-documentation/index.html [[1]]
// ============================================================================

import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.triggers.vcs

version = "2025.11"

// Главный проект
project {
    id("ExampleTeamcity")
    name = "Example TeamCity Project"
    
    // Параметры проекта для безопасного хранения кредов
    params {
        // Nexus credentials — тип password скрывает значение в UI
        password("nexus.user", "credentialsJSON:12a3b456-c7de-890f-123g-4hi567890123")
        password("nexus.password", "credentialsJSON:98f7e654-d3c2-10ab-fedc-ba9876543210")
        param("nexus.url", "http://10.10.10.20:8081")  // Внутренний IP Nexus
        param("nexus.repo.releases", "maven-releases")
        param("nexus.repo.snapshots", "maven-snapshots")
    }
    
    // VCS корень для fork репозитория
    vcsRoot(VcsRoot)
    
    // Конфигурация сборки
    buildType(Build)
    
    // Порядок отображения в UI
    buildTypesOrder = arrayListOf(Build)
}

// VCS Root: GitHub fork
object VcsRoot : GitVcsRoot({
    id("ExampleTeamcity_GitHub")
    name = "GitHub Fork - example-teamcity"
    
    url = "%github.repo.url%"  // Передаётся как параметр при импорте
    branch = "refs/heads/master"
    branchSpec = "+:refs/heads/*"
    
    authMethod = accessToken {
        token = "%github.token%"  // Secure parameter
    }
    
    // Настройки checkout
    checkoutMode = CheckoutMode.ON_SERVER
    useLabelsAsTags = true
})

// Build Configuration: Основная сборка
object Build : BuildType({
    id("ExampleTeamcity_Build")
    name = "Build and Deploy"
    
    // VCS привязка
    vcs {
        root(VcsRoot)
        checkoutMode = CheckoutMode.ON_SERVER
    }
    
    // ==================== STEPS ====================
    
    // Шаг 1: Maven clean test/deploy в зависимости от ветки
    maven {
        name = "Maven: Clean + Test/Deploy"
        
        // 🔑 УСЛОВНАЯ ЛОГИКА: master → deploy, иначе → test
        // Используем параметр ветки из TeamCity
        pomPath = "pom.xml"
        goals = """
            clean
            %teamcity.build.branch.is_default%
            """.trimIndent().let { branchFlag ->
                if (branchFlag.contains("true")) "deploy" else "test"
            }
        """.trimIndent().let { 
            if ("%teamcity.build.branch%" == "refs/heads/master") {
                "clean deploy"
            } else {
                "clean test"
            }
        }
        
        // Maven settings с креденшиалами Nexus
        userSettingsSelection = "app/teamcity/settings.xml"
        
        // Дополнительные JVM параметры
        jvmArgs = "-Xmx512m"
    }
    
    // Шаг 2: Сборка JAR артефакта
    maven {
        name = "Maven: Package JAR"
        enabled = false  // Включается отдельно для master
        pomPath = "pom.xml"
        goals = "package"
        jvmArgs = "-Xmx512m"
    }
    
    // ==================== ARTIFACTS ====================
    
    // Публикация .jar в артефакты сборки
    artifactRules = """
        +:target/*.jar => artifacts/
        +:target/*.jar.md5 => artifacts/
        +:target/*.jar.sha1 => artifacts/
    """.trimIndent()
    
    // ==================== TRIGGERS ====================
    
    // Автозапуск при push в репозиторий
    triggers {
        vcs {
            branchFilter = "+:*"  // Все ветки
            quietPeriodMode = QuietPeriodMode.USE_CUSTOM
            quietPeriod = 60  // 1 минута для группировки коммитов
        }
    }
    
    // ==================== FEATURES ====================
    
    // Публикация статуса сборки в GitHub
    commitStatusPublisher {
        vcsRootExtId = "${VcsRoot.id}"
        publisher = github {
            githubUrl = "https://api.github.com"
            authType = token {
                token = "credentialsJSON:github-status-token-id"
            }
        }
    }
    
    // ==================== PARAMETERS ====================
    
    // Конфигурационные параметры
    params {
        // Ветка по умолчанию
        param("teamcity.build.branch.is_default", "false")
        
        // Maven репозитории из параметров проекта
        param("env.NEXUS_URL", "%nexus.url%")
        param("env.NEXUS_USER", "%nexus.user%")
        param("env.NEXUS_PASSWORD", "%nexus.password%")
    }
    
    // ==================== DEPENDENCIES ====================
    
    // Snapshot dependencies для повторяемости сборок
    snapshotDependencies {
        dependencyRules {
            addRule {
                id = Build.id
                onDependencyFailure = FailureAction.FAIL_TO_START
            }
        }
    }
})