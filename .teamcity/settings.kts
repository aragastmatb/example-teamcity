import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.maven

version = "2025.07"

project {
    description = "test_project_from_video"

    vcsRoot(Netology_HttpsGithubComMlnstwExampleTeamcityZhukov_VcsRoot)
    buildType(Netology_HttpsGithubComMlnstwExampleTeamcityZhukov)
}

object Netology_HttpsGithubComMlnstwExampleTeamcityZhukov_VcsRoot : GitVcsRoot({
    name = "https://github.com/mlnstw/example-teamcity-zhukov"
    url = "https://github.com/mlnstw/example-teamcity-zhukov.git"
    branch = "refs/heads/master"
    branchSpec = """
        +:refs/heads/master
        +:refs/heads/feature/*
    """.trimIndent()
    authMethod = uploadedKey {
        userName = "git"
        uploadedKey = "TeamCity Deployment Key"
    }
})

object Netology_HttpsGithubComMlnstwExampleTeamcityZhukov : BuildType({
    id = DslContext.settingsRoot.id
    name = "https://github.com/mlnstw/example-teamcity-zhukov"
    description = "example-teamcity-zhukov"

    vcs {
        root(Netology_HttpsGithubComMlnstwExampleTeamcityZhukov_VcsRoot)
    }

    steps {
        maven {
            id = "Maven2"
            goals = "%teamcity.build.branch.is_default%: clean deploy; -%teamcity.build.branch.is_default%: clean test"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
            userSettingsSelection = "settings.xml"
        }
    }

    triggers {
        vcs {
            id = "VCS_TRIGGER"
            branchFilter = "+:*"
        }
    }
})