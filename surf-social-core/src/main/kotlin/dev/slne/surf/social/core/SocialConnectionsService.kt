package dev.slne.surf.social.core

import dev.slne.surf.social.api.connection.SocialConnection
import dev.slne.surf.surfapi.core.api.util.requiredService
import it.unimi.dsi.fastutil.objects.ObjectSet
import java.util.*

interface SocialConnectionsService {
    fun <P : SocialConnection> getConnection(minecraftUuid: UUID): P?
    fun getConnections(minecraftUuid: UUID): ObjectSet<SocialConnection>

    companion object {
        val INSTANCE = requiredService<SocialConnectionsService>()
    }
}

val socialConnectionsService get() = SocialConnectionsService.INSTANCE