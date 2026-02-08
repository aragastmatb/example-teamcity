import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs

object ReplyFeatureBuild : BuildType({
    id = AbsoluteId("ReplyFeatureBuild")
    name = "Reply Feature Build"
    description = "Сборка для ветки feature/add_reply"
    
    vcs {
        root(DslContext.settingsRoot)
        cleanCheckout = true
    }
    
    params {
        param("feature.branch", "feature/add_reply")
        param("build.configuration", "reply-service")
        param("version", "1.0.0-SNAPSHOT")
    }
    
    steps {
        script {
            name = "Подготовка окружения"
            scriptContent = """
                echo "Сборка для ветки: %teamcity.build.branch%"
                echo "Имя сборки: %teamcity.buildConfName%"
                echo "ID сборки: %teamcity.build.id%"
                echo ""
                echo "=== НАЧАЛО СБОРКИ REPLY ФУНКЦИОНАЛЬНОСТИ ==="
            """.trimIndent()
        }
        
        script {
            name = "Установка зависимостей"
            scriptContent = """
                echo "Установка зависимостей для reply сервиса..."
                # Здесь могут быть команды npm install, mvn install и т.д.
                sleep 2
                echo "✓ Зависимости установлены"
            """.trimIndent()
        }
        
        script {
            name = "Сборка проекта"
            scriptContent = """
                echo "Сборка reply сервиса..."
                echo "Версия: %version%"
                echo "Ветка: %feature.branch%"
                # Имитация сборки
                echo "✓ Компиляция завершена"
                echo "✓ Тесты пройдены"
                echo "✓ Артефакт создан"
            """.trimIndent()
        }
        
        script {
            name = "Статический анализ кода"
            scriptContent = """
                echo "Проверка качества кода..."
                echo "✓ Проверка стиля кода"
                echo "✓ Поиск уязвимостей"
                echo "✓ Анализ сложности кода"
            """.trimIndent()
        }
        
        script {
            name = "Создание артефакта"
            scriptContent = """
                echo "Создание артефакта для reply функциональности..."
                # Создаем тестовый файл артефакта
                mkdir -p artifacts
                echo "Reply Service v%version%" > artifacts/reply-service.txt
                echo "Build from branch: %feature.branch%" >> artifacts/reply-service.txt
                echo "Build date: $(date)" >> artifacts/reply-service.txt
                echo "Артефакт создан: artifacts/reply-service.txt"
            """.trimIndent()
        }
    }
    
    triggers {
        vcs {
            branchFilter = "+:feature/add_reply"
            triggerRules = """
                -:comment=^\[skip ci\].*
                -:comment=^\[skip teamcity\].*
                +:*
            """.trimIndent()
        }
    }
    
    artifactRules = """
        artifacts/* => reply-artifacts.zip
    """.trimIndent()
    
    features {
        feature {
            type = "perfmon"
        }
        
        feature {
            type = "xml-report"
            param("xmlReportParsing.reportType", "junit")
            param("xmlReportParsing.reportDirs", "**/test-results/*.xml")
        }
        
        feature {
            type = "sauceLabs"
            param("username", "%sauce.username%")
            param("accessKey", "%sauce.accesskey%")
        }
    }
    
    requirements {
        contains("teamcity.agent.jvm.os.name", "Linux")
        noLessThan("teamcity.agent.hardware.memorySizeMb", "2048")
    }
})
