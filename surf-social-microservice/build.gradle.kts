import dev.slne.surf.microservice.gradle.plugin.rabbit.RabbitModule

plugins {
    id("dev.slne.surf.api.gradle.standalone")
    id("dev.slne.surf.microservice")
}

dependencies {
    api(projects.surfSocialCore.surfSocialCoreCommon)
    implementation("io.ktor:ktor-client-cio:2.3.7")
}

surfStandaloneApi {
    withSurfDatabaseR2dbc("1.4.0", "dev.slne.surf.social.libs")
}

surfMicroservice {
    withMicroserviceApi()
    withRabbitModule(RabbitModule.SERVER_API, true)
}