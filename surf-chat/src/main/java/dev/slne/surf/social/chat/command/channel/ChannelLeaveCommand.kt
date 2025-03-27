package dev.slne.surf.social.chat.command.channel

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.social.chat.`object`.Channel
import dev.slne.surf.social.chat.send

class ChannelLeaveCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        playerExecutor { player, _ ->
            val channel: Channel? = Channel.getChannel(player)

            if (channel == null) {
                player.send {
                    appendPrefix()
                    error("Du bist in keinem Nachrichtenkanal.")
                }
                return@playerExecutor
            }

            channel.leave(player.uniqueId)
            player.send {
                appendPrefix()
                primary("Du hast den Nachrichtenkanal ")
                info(channel.name)
                error(" verlassen.")
            }
        }
    }
}
