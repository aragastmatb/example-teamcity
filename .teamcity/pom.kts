// .teamcity/pom.kts
import jetbrains.buildServer.configs.kotlin.v2025_11.*
import jetbrains.buildServer.configs.kotlin.v2025_11.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2025_11.triggers.vcs

// Конфигурация сборки "Build"
object Build : BuildType({
    // Имя сборки в интерфейсе TeamCity
    name = "Build"
    
    // Идентификатор сборки (должен быть уникальным)
    // Используем RelativeId для короткого имени внутри проекта
    id = RelativeId("Build")
    
    // Настройки контроля версий (VCS)
    vcs {
        // Используем VCS root с именем "Team"
        // Этот VCS root должен быть создан в TeamCity
        root(RelativeId("Team"))
    }
    
    // Шаги сборки
    steps {
        // Шаг Maven
        maven {
            // Запускаем clean compile
            goals = "clean compile"
            // Если нужно, можно указать версию Maven
            mavenVersion = defaultProvidedVersion()
            // Или конкретную версию: mavenVersion = MavenVersion.Maven_3_9
        }
    }
    
    // Триггеры (автоматический запуск)
    triggers {
        // Запуск при изменениях в VCS
        vcs {
            // Мониторим все ветки
            branchFilter = """
                +:*
            """.trimIndent()
        }
    }
    
    // Публикация артефактов
    // ВАЖНО: без этого другие сборки не смогут получить артефакты
    artifacts {
        // Правила публикации артефактов
        artifactRules = """
            target/*.jar => artifacts/
            **/target/*.jar => artifacts/
        """.trimIndent()
    }
})

// Конфигурация сборки "Deploy" с зависимостью от артефактов
object Deploy : BuildType({
    name = "Deploy"
    id = RelativeId("Deploy")
    
    vcs {
        root(RelativeId("Team"))
    }
    
    // ЗАВИСИМОСТИ - вот здесь настройка buildRule
    dependencies {
        // Зависимость от артефактов сборки "Build"
        artifacts(RelativeId("Build")) {
            // ВОТ ЭТО buildRule = lastSuccessful()!
            // Получаем артефакты из последней успешной сборки
            buildRule = lastSuccessful()
            
            // ОТКЛЮЧАЕМ IVY XML (важно для TeamCity 2025.11)
            // В новой версии параметр называется по-другому
            // Попробуйте разные варианты если один не работает:
            
            // Вариант 1 (новый):
            useIvyFormat = false
            
            // Или Вариант 2 (старый, но может работать):
            // @Suppress("DEPRECATION")
            // enableIvyXmlSupport = false
            
            // Или Вариант 3 (самый новый):
            // format = ArtifactDependencyFormat.TEAMCITY
            
            // Очищаем директорию перед скачиванием
            cleanDestination = true
            
            // Правила артефактов
            // Берем артефакты из сборки Build и кладем в build-output/
            artifactRules = """
                artifacts/*.jar => build-output/
                artifacts/*.war => build-output/
            """.trimIndent()
        }
    }
    
    steps {
        script {
            name = "Show downloaded artifacts"
            scriptContent = """
                echo "Артефакты скачаны в build-output/:"
                ls -la build-output/
                echo "Всего файлов:"
                find build-output/ -type f | wc -l
            """
        }
        
        script {
            name = "Deploy to Nexus"
            scriptContent = """
                echo "Деплой в Nexus"
                # Здесь может быть ваша команда деплоя
                # Например: mvn deploy
            """
        }
    }
})
