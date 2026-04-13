plugins {
    id("dev.slne.surf.api.gradle.velocity")
}

velocityPluginFile {
    main = "dev.slne.surf.social.velocity.VelocityMain"
    authors = listOf("red")

    pluginDependencies {
        register("surf-rabbitmq-velocity")
    }
}

dependencies {
    api(projects.surfSocialCore.surfSocialCoreClient)
}