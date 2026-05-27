import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.MavenBuildStep

object Build : BuildType({
    id = "Build"
    name = "TeamCity CI/CD Pipeline"
    
    vcs {
        root(DslContext.settingsRoot)
        branchFilter = """
            +:*
            -:pull/*
        """.trimIndent()
    }
    
    params {
        param("env.NEXUS_USER", "%nexus.user%")
        param("env.NEXUS_PASSWORD", "%nexus.password%")
        param("env.NEXUS_URL", "%nexus.url%")
    }
    
    steps {
        // 🔹 Шаг 1: Тесты для ВСЕХ веток, КРОМЕ master
        step(MavenBuildStep {
            name = "Run Tests (non-master)"
            goals = "clean test"
            pomLocation = "pom.xml"
            userSettingsSelection = "teamcity/settings.xml"
            // Условие: выполнить если ветка НЕ равна master
            conditions = listOf(
                EqualsCondition("teamcity.build.branch", "master", invert = true)
            )
        })
        
        // 🔹 Шаг 2: Деплой ТОЛЬКО для master
        step(MavenBuildStep {
            name = "Deploy to Nexus (master only)"
            goals = "clean deploy"
            pomLocation = "pom.xml"
            userSettingsSelection = "teamcity/settings.xml"
            // Условие: выполнить если ветка РАВНА master
            conditions = listOf(
                EqualsCondition("teamcity.build.branch", "master")
            )
        })
    }
    
    // ✅ Публикация JAR в артефактах сборки (пункт 16 задания)
    artifactRules = "+:target/*.jar => artifacts/"
    
    triggers {
        vcs { /* Автозапуск при push */ }
    }
    
    failureConditions {
        testFailure = true
        errorMessage = true
    }
})