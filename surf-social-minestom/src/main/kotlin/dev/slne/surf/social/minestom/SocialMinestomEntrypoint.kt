package dev.slne.surf.social.minestom

import com.google.inject.Inject
import com.google.inject.Singleton
import dev.slne.minestom.lobby.api.plugin.MinestomPluginEntrypoint
import dev.slne.minestom.lobby.api.plugin.annotation.DataDirectory
import dev.slne.surf.social.core.client.ClientSocialInstance
import java.nio.file.Path

@Singleton
class SocialMinestomEntrypoint @Inject constructor(
    @DataDirectory path: Path
) : MinestomPluginEntrypoint {

    init {
        dataPath = path
    }

    override suspend fun start() {
        ClientSocialInstance.clientLoader.onLoad()
    }

    override suspend fun stop() {
        ClientSocialInstance.clientLoader.onDisable()
    }

    companion object {
        @Volatile
        lateinit var dataPath: Path
    }
}
