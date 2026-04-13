package dev.slne.surf.social.api.connection

import kotlinx.serialization.Serializable

@Serializable
abstract class SocialConnection(
    val userName: String
)
