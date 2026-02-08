import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot

version = "2023.05"

project {
    description = "Netology TeamCity Project"
    
    // VCS корень для проекта
    vcsRoot(GitVcsRoot {
        id = AbsoluteId("GitHub_Repo")
        name = "GitHub Repository"
        url = "https://github.com/sapr797/teamcittty.git"
        branch = "refs/heads/main"
        branchSpec = "+:refs/heads/*"
        authMethod = password {
            userName = "sapr797"
            password = "credentialsJSON:******"
        }
    })
    
    // Build configurations будут добавлены ниже через buildType()
 buildType(ReplyFeatureBuild)
}
