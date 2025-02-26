package dev.slne.surf.social.chat.command.channel

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.IntegerArgument
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.jorel.commandapi.kotlindsl.integerArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.social.chat.SurfChat
import dev.slne.surf.social.chat.`object`.Channel
import dev.slne.surf.social.chat.util.MessageBuilder
import dev.slne.surf.social.chat.util.PageableMessageBuilder
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

class ChannelMembersCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        integerArgument("page", 1, Int.MAX_VALUE, true)
        playerExecutor { player, args ->
            val message = PageableMessageBuilder()
            val page = args.getOrDefaultUnchecked("page", 1)
            val channel: Channel? = Channel.getChannelO(player)

            if (channel == null) {
                SurfChat.send(player, MessageBuilder().error("Du bist in keinem Nachrichtenkanal."))
                return@playerExecutor
            }

            var index = 1
            val owner: OfflinePlayer = channel.owner ?: return@playerExecutor

            message.setPageCommand("/channel members " + channel.name + " %page%")
            message.addLine(MessageBuilder().variableValue("$index. ").primary(owner.name ?: owner.uniqueId.toString()).darkSpacer(" (Besitzer)").build())

            for (moderator in channel.moderators) {
                index++
                message.addLine(MessageBuilder().variableValue("$index. ").primary(moderator.name ?: moderator.uniqueId.toString()).darkSpacer(" (Moderator)").build())
            }

            for (member in channel.members) {
                index++
                message.addLine(MessageBuilder().variableValue("$index. ").primary(member.name ?: member.uniqueId.toString()).darkSpacer(" (Mitglied)").build())
            }
            message.send(player, page)
        }
    }
}
