package dev.slne.surf.social.minestom

import dev.slne.surf.api.minestom.plugin.SurfMinestomPlugin
import dev.slne.surf.social.core.client.ClientSocialInstance

object SocialMinestomPlugin : SurfMinestomPlugin() {
    override suspend fun onLoad() {
        ClientSocialInstance.clientLoader.onLoad()
    }

    override suspend fun onDisable() {
        ClientSocialInstance.clientLoader.onDisable()
    }
}
