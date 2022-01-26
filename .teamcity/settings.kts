import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot

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

version = "2021.2"

project {

    vcsRoot(HttpsGithubComPitonixxExampleTeamcityGitRefsHeadsMaster)

    buildType(TestOrDeployToNexus)
}

object TestOrDeployToNexus : BuildType({
    name = "Test or Deploy to Nexus"

    vcs {
        root(HttpsGithubComPitonixxExampleTeamcityGitRefsHeadsMaster)
    }

    steps {
        maven {

            conditions {
                equals("teamcity.build.branch.is_default", "true")
            }
            goals = "clean deploy"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
            userSettingsSelection = "settings.xml"
        }
        maven {
            name = "Test only"

            conditions {
                doesNotContain("teamcity.build.branch", "master")
            }
            goals = "clean test"
        }
    }

    triggers {
        vcs {
        }
    }
})

object HttpsGithubComPitonixxExampleTeamcityGitRefsHeadsMaster : GitVcsRoot({
    name = "https://github.com/Pitonixx/example-teamcity.git#refs/heads/master"
    url = "https://github.com/Pitonixx/example-teamcity.git"
    branch = "refs/heads/master"
    branchSpec = "refs/heads/*"
    authMethod = password {
        userName = "Pitonixxx"
        password = "credentialsJSON:70821f7a-aaf1-424f-8243-98104bcec1a8"
    }
})
