package dev.slne.surf.social.chat.command.channel

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.social.chat.command.argument.ChannelArgument
import dev.slne.surf.social.chat.`object`.Channel
import dev.slne.surf.social.chat.send

class ChannelForceJoinCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission("surf.chat.command.channel.forcejoin")
        withArguments(ChannelArgument("channel"))
        playerExecutor { player, args ->
            val channel = args.getUnchecked<Channel>("channel") ?: return@playerExecutor

            if (Channel.getChannel(player) != null) {
                player.send {
                    appendPrefix()
                    error("Du bist bereits in einem Nachrichtenkanal.")
                }
                return@playerExecutor
            }

            channel.join(player.uniqueId)
            player.send {
                appendPrefix()
                primary("Du bist dem Nachrichtenkanal ")
                info(channel.name)
                success(" beigetreten.")
            }
        }
    }
}
