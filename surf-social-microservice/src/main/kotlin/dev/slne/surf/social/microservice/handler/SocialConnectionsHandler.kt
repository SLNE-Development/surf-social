package dev.slne.surf.social.microservice.handler

import dev.slne.surf.rabbitmq.api.handler.RabbitHandler
import dev.slne.surf.rabbitmq.api.packet.standard.response.primitive.PrimitiveResponse
import dev.slne.surf.social.core.common.rabbit.packet.request.FindDiscordConnectionRequestPacket
import dev.slne.surf.social.core.common.rabbit.packet.request.FindTwitchConnectionRequestPacket
import dev.slne.surf.social.core.common.rabbit.packet.request.UnlinkMinecraftConnectionRequest
import dev.slne.surf.social.core.common.rabbit.packet.response.DiscordConnectionResponsePacket
import dev.slne.surf.social.core.common.rabbit.packet.response.TwitchConnectionResponsePacket
import dev.slne.surf.social.microservice.repository.AccountsRepository
import kotlinx.coroutines.launch

object SocialConnectionsHandler {
    @RabbitHandler
    fun handleFindDiscordConnection(request: FindDiscordConnectionRequestPacket) = request.launch {
        request.respond(
            DiscordConnectionResponsePacket(
                AccountsRepository.findDiscordConnection(
                    request.minecraftUuid
                )
            )
        )
    }

    @RabbitHandler
    fun handleFindTwitchConnection(request: FindTwitchConnectionRequestPacket) = request.launch {
        request.respond(
            TwitchConnectionResponsePacket(
                AccountsRepository.findTwitchConnection(
                    request.minecraftUuid
                )
            )
        )
    }

    @Suppress("DEPRECATION")
    @RabbitHandler
    fun handleUnlinkMinecraftConnectionPacket(request: UnlinkMinecraftConnectionRequest) = request.launch {
        request.respond(PrimitiveResponse.BooleanResponsePacket(AccountsRepository.unlinkMinecraftAccount(request.minecraftUuid)))
    }
}