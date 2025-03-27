package dev.slne.surf.social.chat.command.channel

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.social.chat.command.argument.ChannelMembersArgument
import dev.slne.surf.social.chat.`object`.Channel
import dev.slne.surf.social.chat.send
import org.bukkit.OfflinePlayer

class ChannelPromoteCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withArguments(ChannelMembersArgument("player"))
        playerExecutor { player, args ->
            val channel: Channel? = Channel.getChannel(player)
            val target = args.getUnchecked<OfflinePlayer>("player") ?: return@playerExecutor

            if(channel == null) {
                player.send {
                    appendPrefix()
                    error("Du bist in keinem Nachrichtenkanal.")
                }
                return@playerExecutor
            }

            if (!channel.isOwner(player)) {
                player.send {
                    appendPrefix()
                    error("Du bist nicht der Besitzer des Nachrichtenkanals.")
                }
                return@playerExecutor
            }

            channel.promote(target.uniqueId)

            player.send {
                appendPrefix()
                primary("Du hast den Spieler ")
                info(target.name ?: target.uniqueId.toString())
                success(" befördert.")
            }
            player.send {
                appendPrefix()
                primary("Du wurdest ")
                success("befördert")
            }
        }
    }
}
