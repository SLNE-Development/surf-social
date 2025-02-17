package dev.slne.surf.social.chat.command

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import org.bukkit.entity.Player

class SurfChatChatClearCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission("surf.chat.command.clear")
        executesPlayer(PlayerCommandExecutor { player: Player?, args: CommandArguments? ->
            ChatHistoryService.getInstance().clearChat()
        })
    }
}
