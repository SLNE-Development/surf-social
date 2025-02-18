package dev.slne.surf.social.chat.command

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.IntegerArgument
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.slne.surf.social.chat.service.ChatHistoryService
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class SurfChatDeleteCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission("surf.chat.command.surf-chat.delete")
        withArguments(IntegerArgument("messageID"))
        executesPlayer(PlayerCommandExecutor { _: Player, args: CommandArguments ->
            val messageID = args.getUnchecked<Int>("messageID") ?: return@PlayerCommandExecutor

            Bukkit.getOnlinePlayers().forEach { onlinePlayer: Player ->
                ChatHistoryService.removeMessage(onlinePlayer.uniqueId, messageID)
                ChatHistoryService.resend(onlinePlayer.uniqueId)
            }
        })
    }
}
