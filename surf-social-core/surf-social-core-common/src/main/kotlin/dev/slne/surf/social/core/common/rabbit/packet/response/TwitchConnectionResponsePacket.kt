package dev.slne.surf.social.core.common.rabbit.packet.response

import dev.slne.surf.rabbitmq.api.packet.RabbitResponsePacket
import dev.slne.surf.social.api.connection.impl.TwitchConnection
import kotlinx.serialization.Serializable

@Serializable
data class TwitchConnectionResponsePacket(
    val connection: TwitchConnection?
) : RabbitResponsePacket()
