package dev.slne.surf.social.api.connection.impl

import dev.slne.surf.social.api.connection.SocialConnection

data class DiscordConnection(
    val discordName: String,
    val discordId: Long
): SocialConnection(discordName)
