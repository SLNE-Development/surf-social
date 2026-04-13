package dev.slne.surf.social.microservice.handler

import dev.slne.surf.rabbitmq.api.handler.RabbitHandler
import dev.slne.surf.social.core.common.rabbit.packet.request.FindDiscordConnectionRequestPacket
import dev.slne.surf.social.core.common.rabbit.packet.request.FindTwitchConnectionRequestPacket
import dev.slne.surf.social.core.common.rabbit.packet.response.DiscordConnectionResponsePacket
import dev.slne.surf.social.core.common.rabbit.packet.response.TwitchConnectionResponsePacket
import dev.slne.surf.social.microservice.repository.SocialConnectionsRepository
import kotlinx.coroutines.launch

object SocialConnectionsHandler {
    @RabbitHandler
    fun handleFindDiscordConnection(request: FindDiscordConnectionRequestPacket) = request.launch {
        request.respond(
            DiscordConnectionResponsePacket(
                SocialConnectionsRepository.findDiscordConnection(
                    request.minecraftUuid
                )
            )
        )
    }

    @RabbitHandler
    fun handleFindTwitchConnection(request: FindTwitchConnectionRequestPacket) = request.launch {
        request.respond(
            TwitchConnectionResponsePacket(
                SocialConnectionsRepository.findTwitchConnection(
                    request.minecraftUuid
                )
            )
        )
    }
}