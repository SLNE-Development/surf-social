package dev.slne.surf.social.chat.command.channel

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.social.chat.command.argument.ChannelArgument
import dev.slne.surf.social.chat.`object`.Channel
import dev.slne.surf.social.chat.send

class ChannelForceDeleteCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission("surf.chat.command.channel.forceDelete")
        withArguments(ChannelArgument("channel"))
        playerExecutor { player, args ->
            val channel = args.getUnchecked<Channel>("channel") ?: return@playerExecutor

            if (!channel.delete()) {
                player.send {
                    appendPrefix()
                    error("Der Nachrichtenkanal konnte nicht gelöscht werden.")
                }
                return@playerExecutor
            }
            player.send {
                appendPrefix()
                primary("Der Nachrichtenkanal ")
                info(channel.name)
                error(" wurde gelöscht.")
            }
        }
    }
}
