plugins {
    id("dev.slne.surf.api.gradle.paper-plugin")
}

surfPaperPluginApi {
    mainClass("dev.slne.surf.social.paper.PaperMain")
    generateLibraryLoader(false)

    authors.add("red")
}

dependencies {
    api(projects.surfSocialCore.surfSocialCoreClient)
}