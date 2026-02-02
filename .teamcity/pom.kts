import jetbrains.buildServer.configs.kotlin.v2025_11.*
import jetbrains.buildServer.configs.kotlin.v2025_11.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2025_11.triggers.vcs

object Build : BuildType({
    name = "Build"
    id = RelativeId("Build")
    
    vcs {
        root(RelativeId("Team"))
    }
    
    steps {
        // Шаг 1: Для master ветки - deploy с использованием settings.xml
        maven {
            name = "Deploy on master branch"
            conditions {
                or {
                    equals("teamcity.build.branch", "master")
                    equals("teamcity.build.branch", "refs/heads/master")
                    matches("teamcity.build.branch", "")
                }
            }
            goals = "clean deploy"
            
            // ИСПОЛЬЗУЕМ settings.xml ИЗ TEAMCITY
            mavenSettings = id("Nexus_Settings")  // Имя, которое вы дали при загрузке
            
            // Дополнительные опции для деплоя
            runnerArgs = """
                -DskipTests
                -DaltDeploymentRepository=nexus-releases::default::http://89.169.153.234:8081/repository/maven-releases/
            """.trimIndent()
        }
        
        // Шаг 2: Для других веток - test
        maven {
            name = "Test on other branches"
            conditions {
                and {
                    isNotEmpty("teamcity.build.branch")
                    doesNotEqual("teamcity.build.branch", "master")
                    doesNotEqual("teamcity.build.branch", "refs/heads/master")
                }
            }
            goals = "clean test"
            
            // Для тестов тоже можно использовать settings.xml для загрузки зависимостей
            mavenSettings = id("Nexus_Settings")
        }
    }
    
    triggers {
        vcs {
            branchFilter = """
                +:*
            """.trimIndent()
        }
    }
    
    artifacts {
        artifactRules = """
            target/*.jar => artifacts/
            **/target/*.jar => artifacts/
        """.trimIndent()
    }
})
