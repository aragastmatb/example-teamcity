import jetbrains.buildServer.configs.kotlin.*

object Project : Project({
    id("ExampleTeamcityProject")
    name("Example TeamCity Project")
    
    buildType(Build)
    
    vcsRoot {
        id = DslContext.settingsRoot.id
        branchFilter = """
            +:*
            -:pull/*
        """.trimIndent()
    }
})