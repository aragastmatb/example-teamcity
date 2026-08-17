import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.projectFeatures.githubConnection

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2025.11"

project {

    buildType(Build)

    features {
        githubConnection {
            id = "PROJECT_EXT_5"
            displayName = "GitHub.com (2)"
            clientId = "Ov23liyyrZcvCfq8PsYf"
            clientSecret = "credentialsJSON:b87a0be2-695c-40e6-a4d7-681291edcbc3"
        }
    }
}

object Build : BuildType({
    name = "Build"

    artifactRules = "target/*.jar"

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        maven {
            name = "Build and Test"
            id = "Maven2"
            goals = "clean install"
        }
        maven {
            name = "Maven2_1"
            id = "Maven2_1"
            goals = "clean deploy"
            userSettingsSelection = "userSettingsSelection:byPath"
            userSettingsPath = "/home/vm-2/deploy-settings.xml"
        }
    }
})
