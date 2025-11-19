package dev.slne.surf.social.core

import dev.slne.surf.social.api.connection.SocialConnection
import it.unimi.dsi.fastutil.objects.ObjectSet
import java.util.UUID

interface SocialConnectionsService {
    fun <P : SocialConnection> getConnection(minecraftUuid: UUID): P?
    fun <P : SocialConnection> setConnection(minecraftUuid: UUID, connection: P): P
    fun getConnections(minecraftUuid: UUID): ObjectSet<SocialConnection>
}