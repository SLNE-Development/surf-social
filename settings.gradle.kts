rootProject.name = "surf-social"

include("surf-social-api")
include("surf-social-core:surf-social-core-common")
include("surf-social-core:surf-social-core-client")
include("surf-social-paper")
include("surf-social-velocity")
include("surf-social-minestom")

include("surf-social-microservice")


pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://reposilite.slne.dev/releases")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    id("dev.slne.surf.api.gradle.settings") version "+"
}