plugins {
    id("dev.slne.surf.api.gradle.velocity")
}

velocityPluginFile {
    main = "dev.slne.surf.social.velocity.VelocityMain"
    authors = listOf("red")
}

surfVelocityApi {
    withCoreVelocity()
}

dependencies {
    api(projects.surfSocialCore.surfSocialCoreClient)
}