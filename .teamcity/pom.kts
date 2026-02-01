// .teamcity/pom.kts
import jetbrains.buildServer.configs.kotlin.v2025_11.*
import jetbrains.buildServer.configs.kotlin.v2025_11.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2025_11.triggers.vcs

object Build : BuildType({
    name = "Build"
    id = RelativeId("Build")
    
    vcs {
        root(RelativeId("Team"))
    }
    
    // ДВА ШАГА С УСЛОВИЯМИ:
    steps {
        // Шаг 1: Деплой для master ветки
        maven {
            name = "Deploy on master"
            conditions {
                // Выполняется только для master ветки
                or {
                    equals("teamcity.build.branch", "master")
                    equals("teamcity.build.branch", "refs/heads/master")
                    // Для дефолтной ветки (если параметр пустой)
                    matches("teamcity.build.branch", "")
                }
            }
            goals = "clean deploy"
        }
        
        // Шаг 2: Тесты для других веток
        maven {
            name = "Test on other branches"
            conditions {
                // Выполняется для всех веток кроме master
                and {
                    isNotEmpty("teamcity.build.branch")
                    doesNotEqual("teamcity.build.branch", "master")
                    doesNotEqual("teamcity.build.branch", "refs/heads/master")
                }
            }
            goals = "clean test"
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

object Deploy : BuildType({
    name = "Deploy"
    id = RelativeId("Deploy")
    
    vcs {
        root(RelativeId("Team"))
    }
    
    dependencies {
        artifacts(RelativeId("Build")) {
            buildRule = lastSuccessful()
            useIvyFormat = false
            cleanDestination = true
            artifactRules = "artifacts/*.jar => build-output/"
        }
    }
    
    steps {
        script {
            name = "Show downloaded artifacts"
            scriptContent = """
                echo "Artifacts for deployment:"
                ls -la build-output/
            """
        }
    }
})
