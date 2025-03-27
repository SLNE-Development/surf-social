package dev.slne.surf.social.chat.command

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.integerArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.social.chat.service.ChatHistoryService
import java.util.*

class SurfChatDeleteCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission("surf.chat.command.surf-chat.delete")
        integerArgument("messageID")
        playerExecutor { player, args ->
            val messageID = args.getUnchecked<String>("messageID") ?: return@playerExecutor

            ChatHistoryService.removeMessage(player.uniqueId, UUID.fromString(messageID))
            ChatHistoryService.resend(player.uniqueId)
        }
    }
}
