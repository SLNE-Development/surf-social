package dev.slne.surf.social.velocity.config

import dev.slne.surf.api.core.config.manager.SpongeConfigManager
import dev.slne.surf.api.core.config.surfConfigApi
import dev.slne.surf.social.velocity.plugin

class SocialConfigManager {
    private val configManager: SpongeConfigManager<SocialConfig>

    init {
        surfConfigApi.createSpongeYmlConfig(
            SocialConfig::class.java,
            plugin.dataPath,
            "config.yml"
        )
        configManager = surfConfigApi.getSpongeConfigManagerForConfig(
            SocialConfig::class.java
        )
        reload()
    }

    fun reload() {
        configManager.reloadFromFile()
    }

    val config get() = configManager.config
}