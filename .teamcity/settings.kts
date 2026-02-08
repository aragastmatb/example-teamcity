import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.triggers.vcs

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

    buildType(Build1)
    buildType(Build2)
    buildType(Build)
}

object Build : BuildType({
    name = "Build"

    enablePersonalBuilds = false
    type = BuildTypeSettings.Type.DEPLOYMENT
    maxRunningBuilds = 1

    params {
        param("branch.condition", "%teamcity.build.branch%")
    }

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        maven {
            name = "Deploy to Nexus"
            id = "Deploy_to_Nexus"

            conditions {
                contains("teamcity.build.branch", "master")
            }
            goals = "clean deploy"
            coverageEngine = jacoco {
                classLocations = "target/classes"
            }
        }
        maven {
            name = "Run Tests"
            id = "Maven2"

            conditions {
                doesNotContain("teamcity.build.branch", "master")
            }
            goals = "clean test"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
            coverageEngine = jacoco {
                classLocations = "target/classes"
            }
        }
    }

    features {
        perfmon {
        }
    }
})

object Build1 : BuildType({
    name = "Build 1"

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        maven {
            id = "Maven2"
            goals = "clean test"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
            coverageEngine = jacoco {
                classLocations = "target/classes"
            }
        }
    }

    features {
        perfmon {
        }
    }
})

object Build2 : BuildType({
    name = "Build 2"

    artifactRules = "target/*.jar"

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        maven {
            id = "Maven2"

            conditions {
                equals("teamcity.build.branch", "refs/heads/master")
            }
            goals = "clean deploy"
            coverageEngine = jacoco {
                classLocations = "target/classes"
            }
        }
        maven {
            name = "Run Tests"
            id = "Run_Tests"

            conditions {
                equals("teamcity.build.branch.is_default", "false")
                doesNotEqual("teamcity.build.branch", "refs/heads/master")
            }
            goals = "clean test"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
            coverageEngine = jacoco {
                classLocations = "target/classes"
            }
        }
    }

    triggers {
        vcs {
        }
    }

    features {
        perfmon {
        }
    }
})
