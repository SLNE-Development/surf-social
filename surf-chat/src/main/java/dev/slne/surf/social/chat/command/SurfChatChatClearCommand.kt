package dev.slne.surf.social.chat.command

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.social.chat.send
import dev.slne.surf.social.chat.service.ChatHistoryService

class SurfChatChatClearCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission("surf.chat.command.clear")
        playerExecutor { player, _ ->
            ChatHistoryService.clearChat()
            player.send {
                appendPrefix()
                primary("Der Chat wurde ")
                success("geleert.")
            }
        }
    }
}
