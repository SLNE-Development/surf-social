package dev.slne.surf.social.velocity.config

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class SocialConfig(
    val twitchClientId: String = "",
    val encryptionSecret: String = ""
)
