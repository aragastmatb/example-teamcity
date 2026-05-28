import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.MavenBuildStep

object Build : BuildType({
    id("Build")
    name("TeamCity CI/CD Pipeline")
    
    vcs {
        root(DslContext.settingsRoot)
    }
    
    // Параметры Maven-шагов автоматически берутся из проекта
    steps {
        step(MavenBuildStep {
            name = "Run Tests (non-master)"
            goals = "clean test"
            pomLocation = "pom.xml"
            userSettingsSelection = "teamcity/settings.xml"
            
            conditions {
                doesNotEqual("teamcity.build.branch", "master")
            }
        })
        
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
    
    artifactRules = "+:target/*.jar => artifacts/"
    
    triggers {
        vcs { branchFilter = "+:*" }
    }
    
    failureConditions {
        testFailure = true
        errorMessage = true
    }
})