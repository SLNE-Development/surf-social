package dev.slne.surf.social.core.client.api

import com.google.auto.service.AutoService
import dev.slne.surf.social.api.SurfSocialApi
import dev.slne.surf.social.api.connection.SocialConnection
import dev.slne.surf.social.core.client.service.SocialConnectionsService
import java.util.*

@AutoService(SurfSocialApi::class)
class SurfSocialApiImpl : SurfSocialApi {
    override suspend fun <T : SocialConnection> findConnection(
        connectionClass: Class<T>,
        minecraftUuid: UUID
    ): T? = SocialConnectionsService.findConnection(connectionClass, minecraftUuid)
}
