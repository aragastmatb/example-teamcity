import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.vcs.GitVcsRoot

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

    vcsRoot(GitGithubComVndr3wExampleTeamcityGitRefsHeadsMaster)

    buildType(Build)
}

object Build : BuildType({
    name = "Build"

    vcs {
        root(GitGithubComVndr3wExampleTeamcityGitRefsHeadsMaster)
    }

    steps {
        maven {
            name = "clean_deploy"
            id = "clean_deploy"

            conditions {
                contains("teamcity.build.branch", "master")
            }
            goals = "clean deploy"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
            userSettingsSelection = "settings.xml"
        }
        maven {
            id = "Maven2"
            goals = "clean test"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
        }
    }
})

object GitGithubComVndr3wExampleTeamcityGitRefsHeadsMaster : GitVcsRoot({
    name = "git@github.com:Vndr3w/example-teamcity.git#refs/heads/master"
    url = "git@github.com:Vndr3w/example-teamcity.git"
    branch = "refs/heads/master"
    branchSpec = "refs/heads/*"
    authMethod = uploadedKey {
        uploadedKey = "ssh-key-1776007530027"
    }
})
