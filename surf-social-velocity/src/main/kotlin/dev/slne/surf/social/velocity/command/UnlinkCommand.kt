package dev.slne.surf.social.velocity.command

import dev.jorel.commandapi.kotlindsl.commandTree
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.api.velocity.command.executors.playerExecutorSuspend
import dev.slne.surf.social.core.client.service.SocialConnectionsService
import dev.slne.surf.social.core.common.rabbit.rpc.SocialConnectionRpc
import dev.slne.surf.social.velocity.permission.SocialPermissions
import dev.slne.surf.social.velocity.plugin
import dev.slne.surf.social.velocity.redis.DiscordRolesPublisher
import dev.slne.surf.social.velocity.redis.SocialRedisService
import kotlinx.coroutines.CancellationException

fun unlinkCommand() = commandTree("unlink") {
    withPermission(SocialPermissions.COMMAND_UNLINK)

    playerExecutorSuspend { player, arguments ->
        val result = SocialConnectionsService.unlinkMinecraftAccount(player.uniqueId)

        if (result == SocialConnectionRpc.UnlinkResult.NotLinked) {
            player.sendText {
                appendErrorPrefix()
                error("Du hast keinen verknüpften Account.")
            }

            return@playerExecutorSuspend
        }

        require(result is SocialConnectionRpc.UnlinkResult.Unlinked) {
            "Unexpected result type: ${result::class.java.name}"
        }

        val discordId = result.discordId

        if (discordId != null) {
            try {
                SocialRedisService.discordRolesPublisher.publish(
                    DiscordRolesPublisher.Request.Action.REMOVE,
                    discordId
                )
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (throwable: Throwable) {
                plugin.logger.error(
                    "Failed to publish the discord role removal for user $discordId",
                    throwable
                )
            }
        }

        player.sendText {
            appendSuccessPrefix()
            success("Dein Minecraft Account wurde entkoppelt.")
        }
    }
}