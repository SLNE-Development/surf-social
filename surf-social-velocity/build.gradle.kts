plugins {
    id("dev.slne.surf.api.gradle.velocity")
}

velocityPluginFile {
    main = "dev.slne.surf.social.velocity.VelocityMain"
    authors = listOf("red")
}

surfVelocityApi {
    withCoreVelocity()
    withSurfRedis()
}

dependencies {
    api(projects.surfSocialCore.surfSocialCoreClient)
}