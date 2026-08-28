import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.projectFeatures.githubAppConnection

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

version = "2026.1"

project {

    buildType(Netology_ExampleTeamcity)

    features {
        githubAppConnection {
            id = "PROJECT_EXT_2"
            displayName = "TeamCityformunk"
            appId = "4751901"
            clientId = "Iv23linextnB7ZxkkVG2"
            clientSecret = "credentialsJSON:045d2984-a676-47fe-bd05-dce4bfd4cd90"
            privateKey = "credentialsJSON:0a24ae78-eab2-49b0-8a21-6382180fd242"
            webhookSecret = "credentialsJSON:1a179735-2600-46ec-a6a1-2f23a4188801"
            ownerUrl = "https://github.com/mynkyshonok"
            useUniqueCallback = true
        }
    }
}

object Netology_ExampleTeamcity : BuildType({
    id("ExampleTeamcity")
    name = "example-teamcity"

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        maven {
            name = "non_master"
            id = "Maven2_1"

            conditions {
                doesNotEqual("teamcity.build.branch", "master")
            }
            goals = "clean test"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
        }
        maven {
            id = "Maven2"

            conditions {
                equals("teamcity.build.branch", "master")
            }
            goals = "clean deploy"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
            userSettingsSelection = "nexus"
        }
    }
})
