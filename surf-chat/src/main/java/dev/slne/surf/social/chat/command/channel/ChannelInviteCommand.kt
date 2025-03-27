package dev.slne.surf.social.chat.command.channel

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.OfflinePlayerArgument
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.social.chat.SurfChat
import dev.slne.surf.social.chat.`object`.Channel
import dev.slne.surf.social.chat.send
import dev.slne.surf.social.chat.util.MessageBuilder
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.clickSuggestsCommand
import net.kyori.adventure.text.Component
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

class ChannelInviteCommand(commandName: String) : CommandAPICommand(commandName) {
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

            channel.invite(target.uniqueId)

            player.send {
                appendPrefix()
                primary("Du hast ")
                info(target.name ?: target.uniqueId.toString())
                primary(" in den Nachrichtenkanal ")
                info(channel.name)
                success(" eingeladen.")
            }

            player.send {
                appendPrefix()
                primary("Du wurdest in den Nachrichtenkanal ")
                info(channel.name)
                success(" eingeladen. ")
                append {
                    darkSpacer("[")
                    success("Beitreten")
                    darkSpacer("]")
                    clickSuggestsCommand("/channel accept " + channel.name)
                    hoverEvent(Component.text("Klicke, um beizutreten", Colors.INFO))
                }
                success("Klicke, um beizutreten")
            }
        }
    }
}
