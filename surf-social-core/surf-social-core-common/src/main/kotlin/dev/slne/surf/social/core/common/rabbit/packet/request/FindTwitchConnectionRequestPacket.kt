package dev.slne.surf.social.core.common.rabbit.packet.request

import dev.slne.surf.api.core.serializer.java.uuid.SerializableUUID
import dev.slne.surf.rabbitmq.api.packet.RabbitRequestPacket
import dev.slne.surf.social.core.common.rabbit.packet.response.TwitchConnectionResponsePacket
import kotlinx.serialization.Serializable

@Serializable
data class FindTwitchConnectionRequestPacket(
    val minecraftUuid: SerializableUUID
) : RabbitRequestPacket<TwitchConnectionResponsePacket>()