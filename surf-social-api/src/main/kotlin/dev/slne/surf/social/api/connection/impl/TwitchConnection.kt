package dev.slne.surf.social.api.connection.impl

import dev.slne.surf.social.api.connection.SocialConnection
import kotlinx.serialization.Serializable

@Serializable
data class TwitchConnection(
    val twitchName: String,
    val twitchId: Long
) : SocialConnection(twitchName)