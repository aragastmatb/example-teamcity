import jetbrains.buildServer.configs.kotlin.*

version = "2023.11"

project {
    vcsRoot(DslContext.settingsRoot)
    subProject(Project)
    
    params {
        param("env.NEXUS_USER", "%nexus.user%")
        param("env.NEXUS_PASSWORD", "%nexus.password%")
        param("env.NEXUS_URL", "%nexus.url%")
    }
}