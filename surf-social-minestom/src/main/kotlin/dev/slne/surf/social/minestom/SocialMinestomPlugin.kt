package dev.slne.surf.social.minestom

import com.google.auto.service.AutoService
import dev.slne.minestom.lobby.api.plugin.MinestomPlugin
import dev.slne.minestom.lobby.api.plugin.annotation.MinestomPluginMeta

@AutoService(MinestomPlugin::class)
@MinestomPluginMeta(
    "surf-social-minestom",
    dependsOn = [
        "surf-api-minestom",
        "surf-rabbitmq-minestom"
    ]
)
class SocialMinestomPlugin : MinestomPlugin(SocialMinestomEntrypoint::class.java)
