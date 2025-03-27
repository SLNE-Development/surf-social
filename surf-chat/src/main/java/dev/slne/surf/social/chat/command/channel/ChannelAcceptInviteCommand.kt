package dev.slne.surf.social.chat.command.channel

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.social.chat.command.argument.ChannelInviteArgument
import dev.slne.surf.social.chat.`object`.Channel
import dev.slne.surf.social.chat.send

class ChannelAcceptInviteCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withArguments(ChannelInviteArgument("channel"))

        playerExecutor { player, args ->
            val channel = args.getUnchecked<Channel>("channel") ?: return@playerExecutor

            if (!channel.hasInvite(player)) {
                player.send {
                    appendPrefix()
                    primary("Du hast keine Einladung in den Nachrichtenkanal ")
                    info(channel.name)
                    primary(" erhalten.")
                }
                return@playerExecutor
            }

            channel.acceptInvite(player)
            player.send {
                appendPrefix()
                primary("Du hast die Einladung in den Nachrichtenkanal ")
                info(channel.name)
                success(" angenommen.")
            }
        }
    }
}
