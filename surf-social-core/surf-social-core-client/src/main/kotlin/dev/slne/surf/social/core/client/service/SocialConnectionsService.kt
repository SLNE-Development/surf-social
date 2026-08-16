package dev.slne.surf.social.core.client.service

import dev.slne.surf.social.api.connection.SocialConnection
import dev.slne.surf.social.api.connection.impl.DiscordConnection
import dev.slne.surf.social.api.connection.impl.TwitchConnection
import dev.slne.surf.social.core.client.ClientSocialInstance
import dev.slne.surf.social.core.common.rabbit.packet.request.FindDiscordConnectionRequestPacket
import dev.slne.surf.social.core.common.rabbit.packet.request.FindTwitchConnectionRequestPacket
import dev.slne.surf.social.core.common.rabbit.packet.request.UnlinkMinecraftConnectionRequest
import java.util.*

object SocialConnectionsService {
    @Suppress("UNCHECKED_CAST")
    suspend fun <T : SocialConnection> findConnection(
        connectionClass: Class<T>,
        minecraftUuid: UUID
    ): T? = when (connectionClass) {
        DiscordConnection::class.java -> ClientSocialInstance.rabbitApi.sendRequest(
            FindDiscordConnectionRequestPacket(minecraftUuid)
        ).connection as? T

        TwitchConnection::class.java -> ClientSocialInstance.rabbitApi.sendRequest(
            FindTwitchConnectionRequestPacket(minecraftUuid)
        ).connection as? T

        else -> null
    }

    suspend fun unlinkMinecraftAccount(minecraftUuid: UUID) =
        ClientSocialInstance.rabbitApi.sendRequest(
            UnlinkMinecraftConnectionRequest(minecraftUuid)
        ).value
}