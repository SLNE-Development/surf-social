plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin")
}

surfPaperPluginApi {
    mainClass("dev.slne.surf.social.bukkit.BukkitMain")
    generateLibraryLoader(false)

    authors.add("red")
}