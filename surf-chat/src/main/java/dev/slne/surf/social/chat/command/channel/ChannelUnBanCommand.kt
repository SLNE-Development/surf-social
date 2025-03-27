package dev.slne.surf.social.chat.command.channel

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.offlinePlayerArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.social.chat.`object`.Channel
import dev.slne.surf.social.chat.send
import org.bukkit.OfflinePlayer

class ChannelUnBanCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        offlinePlayerArgument("player")
        playerExecutor { player, args ->
            val target = args.getUnchecked<OfflinePlayer>("player") ?: return@playerExecutor
            val channel: Channel? = Channel.getChannel(player)

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

            channel.unban(target.uniqueId)
            player.send {
                appendPrefix()
                primary("Du hast ")
                info(target.name ?: target.uniqueId.toString())
                primary(" im Nachrichtenkanal ")
                info(channel.name)
                success(" entbannt.")
            }
            player.send {
                appendPrefix()
                primary("Du wurdest im Nachrichtenkanal ")
                info(channel.name)
                error(" entbannt.")
            }
        }
    }
}
