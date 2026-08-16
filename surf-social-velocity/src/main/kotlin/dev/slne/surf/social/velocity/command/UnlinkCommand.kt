package dev.slne.surf.social.velocity.command

import dev.jorel.commandapi.kotlindsl.commandTree
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.api.velocity.command.executors.playerExecutorSuspend
import dev.slne.surf.social.core.client.service.SocialConnectionsService
import dev.slne.surf.social.velocity.permission.SocialPermissions

fun unlinkCommand() = commandTree("unlink") {
    withPermission(SocialPermissions.COMMAND_UNLINK)

    playerExecutorSuspend { player, arguments ->
        if (SocialConnectionsService.unlinkMinecraftAccount(player.uniqueId)) {
            player.sendText {
                appendSuccessPrefix()
                success("Dein Minecraft Account wurde entkoppelt.")
            }
        } else {
            player.sendText {
                appendErrorPrefix()
                error("Du hast keinen verknüpften Account.")
            }
        }
    }
}