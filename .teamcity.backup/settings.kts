// .teamcity/settings.kts
version = "2023.05"  // Версия TeamCity DSL

// Определяем проект
project {
    // Регистрируем все конфигурации сборок
    buildType(Build)
    buildType(Deploy)
}

// Конфигурация сборки "Build"
object Build : BuildType({
    name = "Build"  // Имя в интерфейсе
    id = RelativeId("Build")  // Идентификатор
    
    vcs {
        root(RelativeId("Team"))  // Привязка к VCS
    }
    
    steps {
        maven {
            goals = "clean compile"  // Команды Maven
        }
    }
    
    // Публикация артефактов (важно!)
    artifacts {
        artifactRules = """
            target/*.jar => artifacts/
            **/target/*.jar => artifacts/
        """.trimIndent()
    }
})

// Конфигурация сборки "Deploy" с зависимостью
object Deploy : BuildType({
    name = "Deploy"
    
    dependencies {
        // ВОТ ЗДЕСЬ прописываем зависимость!
        artifacts(RelativeId("Build")) {
            // Это параметр buildRule:
            buildRule = lastSuccessful()  // ← ВОТ ОН!
            
            // Дополнительные параметры:
            enableIvyXmlSupport = false
            cleanDestination = true
            
            // Правила артефактов:
            artifactRules = """
                artifacts/*.jar => build-output/
            """.trimIndent()
        }
    }
    
    steps {
        script {
            name = "Deploy artifacts"
            scriptContent = """
                echo "Artifacts downloaded to build-output/"
                ls -la build-output/
            """
        }
    }
})
