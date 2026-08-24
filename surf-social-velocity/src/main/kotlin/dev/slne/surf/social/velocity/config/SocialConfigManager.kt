package dev.slne.surf.social.velocity.config

import dev.slne.surf.api.core.config.manager.SpongeConfigManager
import dev.slne.surf.api.core.config.surfConfigApi
import java.nio.file.Path

class SocialConfigManager(dataPath: Path) {
    private val configManager: SpongeConfigManager<SocialConfig>

    init {
        surfConfigApi.createSpongeYmlConfig(
            SocialConfig::class.java,
            dataPath,
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
