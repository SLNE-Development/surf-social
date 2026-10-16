import dev.slne.surf.api.gradle.util.slneReleases

plugins {
    id("dev.slne.surf.api.gradle.minestom")
}

minestomPluginFile {
    main = "dev.slne.surf.social.minestom.SocialMinestomPlugin"
    authors = listOf("red")

    pluginDependencies {
        register("surf-rabbitmq-minestom")
    }
}

dependencies {
    api(projects.surfSocialCore.surfSocialCoreClient)
}

publishing {
    repositories {
        slneReleases()
    }
}
