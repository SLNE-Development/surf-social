package dev.slne.surf.social.chat.command.channel

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.offlinePlayerArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.social.chat.command.argument.ChannelArgument
import dev.slne.surf.social.chat.`object`.Channel
import dev.slne.surf.social.chat.send
import org.bukkit.OfflinePlayer

class ChannelMoveCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        offlinePlayerArgument("player")
        withOptionalArguments(ChannelArgument("channel"))
        withPermission("surf.chat.command.channel.move")

        playerExecutor { player, args ->
            val target = args.getUnchecked<OfflinePlayer>("player") ?: return@playerExecutor
            val channel = args.getUnchecked<Channel>("channel") ?: return@playerExecutor

            channel.move(target.uniqueId, channel)

            player.send {
                appendPrefix()
                primary("Du hast ")
                info(target.name ?: target.uniqueId.toString())
                primary(" in den Nachrichtenkanal ")
                info(channel.name)
                success(" verschoben.")
            }
            player.send {
                appendPrefix()
                primary("Du wurdest in den Nachrichtenkanal ")
                info(channel.name)
                success(" verschoben.")
            }
        }
    }
}
