package dev.slne.surf.social.api

import dev.slne.surf.api.core.util.requiredService
import dev.slne.surf.social.api.connection.SocialConnection
import java.util.*

private val api = requiredService<SurfSocialApi>()

interface SurfSocialApi {
    suspend fun <T : SocialConnection> findConnection(
        connectionClass: Class<T>,
        minecraftUuid: UUID
    ): T?

    companion object : SurfSocialApi by api
}

suspend inline fun <reified T : SocialConnection> SurfSocialApi.findConnection(
    minecraftUuid: UUID
): T? = findConnection(T::class.java, minecraftUuid)