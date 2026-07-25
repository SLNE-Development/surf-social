package dev.slne.surf.social.velocity.config

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class SocialConfig(
    val twitchClientId: String = "",
    val discordClientId: String = "",
    val encryptionSecret: String = "",
    val minecraftAuthToken: String = ""
)
