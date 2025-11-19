package dev.slne.surf.social.api.connection.impl

import dev.slne.surf.social.api.connection.SocialConnection

data class TwitchConnection(
    val twitchName: String,
    val twitchId: Long
) : SocialConnection(twitchName)