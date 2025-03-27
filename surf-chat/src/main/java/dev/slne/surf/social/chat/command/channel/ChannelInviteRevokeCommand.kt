package dev.slne.surf.social.chat.command.channel

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.OfflinePlayerArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.social.chat.`object`.Channel
import dev.slne.surf.social.chat.send
import org.bukkit.OfflinePlayer

class ChannelInviteRevokeCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withArguments(OfflinePlayerArgument("player"))
        playerExecutor { player, args ->
            val channel: Channel? = Channel.getChannel(player)
            val target = args.getUnchecked<OfflinePlayer>("player") ?: return@playerExecutor

            if (channel == null) {
                player.send {
                    appendPrefix()
                    error("Du bist in keinem Nachrichtenkanal.")
                }
                return@playerExecutor
            }

            if (!channel.isModerator(player) && !channel.isOwner(player)) {
                player.send {
                    appendPrefix()
                    error("Du bist nicht der Moderator oder Besitzer des Nachrichtenkanals.")
                }
                return@playerExecutor
            }

            channel.revokeInvite(target.uniqueId)

            player.send {
                appendPrefix()
                primary("Du hast die Einladung für ")
                info(target.name ?: target.uniqueId.toString())
                primary(" in den Nachrichtenkanal ")
                info(channel.name)
                success(" zurückgezogen.")
            }
            player.send {
                appendPrefix()
                primary("Deine Einladung in den Nachrichtenkanal ")
                info(channel.name)
                success(" wurde zurückgezogen.")
            }
        }
    }
}
