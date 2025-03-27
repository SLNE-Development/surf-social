package dev.slne.surf.social.chat.command.channel

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.social.chat.SurfChat
import dev.slne.surf.social.chat.command.argument.ChannelMembersArgument
import dev.slne.surf.social.chat.`object`.Channel
import dev.slne.surf.social.chat.send
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import org.bukkit.OfflinePlayer

class ChannelBanCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withArguments(ChannelMembersArgument("player"))
        playerExecutor { player, args ->
            val channel: Channel? = Channel.getChannel(player)
            val target = args.getUnchecked<OfflinePlayer>("player") ?: return@playerExecutor

            if (channel == null) {
                SurfChat.send(player, buildText {
                    appendPrefix()
                    error("Du bist in keinem Nachrichten.")
                })
                return@playerExecutor
            }

            if (!channel.isModerator(player) && !channel.isOwner(player)) {
                player.send {
                    appendPrefix()
                    primary("Du bist ")
                    error("kein Moderator")
                    primary(" in diesem Kanal.")
                }
                return@playerExecutor
            }

            channel.ban(target.uniqueId)

            player.send {
                appendPrefix()
                primary("Du hast ")
                info(target.name ?: target.uniqueId.toString())
                primary(" aus dem Nachrichtenkanal ")
                info(channel.name)
                error(" verbannt.")
            }
            player.send {
                appendPrefix()
                primary("Du wurdest aus dem Nachrichtenkanal ")
                info(channel.name)
                error(" verbannt.")
            }
        }

    }
}
