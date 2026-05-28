import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.MavenBuildStep

object Build : BuildType({
    id("Build")
    name("TeamCity CI/CD Pipeline")
    
    vcs {
        root(DslContext.settingsRoot)
    }
    
    params {
        param("env.NEXUS_USER", "%nexus.user%")
        param("env.NEXUS_PASSWORD", "%nexus.password%")
        param("env.NEXUS_URL", "%nexus.url%")
    }
    
    steps {
        // 🔹 Тесты для ВСЕХ веток, КРОМЕ master
        step(MavenBuildStep {
            name = "Run Tests (non-master)"
            goals = "clean test"
            pomLocation = "pom.xml"
            userSettingsSelection = "teamcity/settings.xml"
            
            conditions {
                doesNotEqual("teamcity.build.branch", "master")
            }
        })
        
        // 🔹 Деплой ТОЛЬКО для master
        step(MavenBuildStep {
            name = "Deploy to Nexus (master only)"
            goals = "clean deploy"
            pomLocation = "pom.xml"
            userSettingsSelection = "teamcity/settings.xml"
            
            conditions {
                equals("teamcity.build.branch", "master")
            }
        })
    }
    
    // ✅ Публикация JAR
    artifactRules = "+:target/*.jar => artifacts/"
    
    triggers {
        vcs {
            branchFilter = "+:*"
        }
    }
    
    failureConditions {
        testFailure = true
        errorMessage = true
    }
})