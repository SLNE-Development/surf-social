package dev.slne.surf.social.chat.command.channel

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.social.chat.SurfChat
import dev.slne.surf.social.chat.`object`.Channel
import dev.slne.surf.social.chat.send
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText

class ChannelDeleteCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        playerExecutor { player, _ ->
            val channel: Channel? = Channel.getChannel(player)
            if (channel == null) {
                SurfChat.send(player, buildText {
                    appendPrefix()
                    error("Du bist in keinem Nachrichtenkanal.")
                })
                return@playerExecutor
            }

            if (!channel.isOwner(player)) {
                player.send {
                    appendPrefix()
                    error("Du bist nicht der Besitzer des Nachrichtenkanals.")
                }
                return@playerExecutor
            }

            if (!channel.delete()) {
                player.send {
                    appendPrefix()
                    error("Der Nachrichtenkanal konnte nicht gelöscht werden.")
                }
                return@playerExecutor
            }

            player.send {
                appendPrefix()
                primary("Du hast den Nachrichtenkanal ")
                info(channel.name)
                error(" gelöscht.")
            }

        }

    }
}
