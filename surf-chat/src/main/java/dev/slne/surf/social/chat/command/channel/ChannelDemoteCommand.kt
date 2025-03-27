package dev.slne.surf.social.chat.command.channel

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.social.chat.SurfChat
import dev.slne.surf.social.chat.command.argument.ChannelMembersArgument
import dev.slne.surf.social.chat.`object`.Channel
import dev.slne.surf.social.chat.send
import dev.slne.surf.social.chat.util.MessageBuilder
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

class ChannelDemoteCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withArguments(ChannelMembersArgument("player"))
        playerExecutor { player, args ->
            val channel: Channel? = Channel.getChannel(player)
            val target = args.getUnchecked<OfflinePlayer>("player") ?: return@playerExecutor

            if (channel == null) {
                SurfChat.send(player, buildText {
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

            channel.demote(target.uniqueId)

            player.send {
                appendPrefix()
                primary("Du hast ")
                info(target.name ?: target.uniqueId.toString())
                primary(" degradiert.")
            }
            player.send {
                appendPrefix()
                primary("Du wurdest ")
                error("degradiert.")
            }
        }
    }
}
