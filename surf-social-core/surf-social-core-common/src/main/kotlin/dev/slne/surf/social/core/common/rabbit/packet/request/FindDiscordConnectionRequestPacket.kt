package dev.slne.surf.social.core.common.rabbit.packet.request

import dev.slne.surf.api.core.serializer.java.uuid.SerializableUUID
import dev.slne.surf.rabbitmq.api.packet.RabbitRequestPacket
import dev.slne.surf.social.core.common.rabbit.packet.response.DiscordConnectionResponsePacket
import kotlinx.serialization.Serializable

@Serializable
data class FindDiscordConnectionRequestPacket(
    val minecraftUuid: SerializableUUID
) : RabbitRequestPacket<DiscordConnectionResponsePacket>()