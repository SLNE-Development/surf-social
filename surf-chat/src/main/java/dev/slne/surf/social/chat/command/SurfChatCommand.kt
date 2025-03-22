package dev.slne.surf.social.chat.command

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.social.chat.command.config.*

class SurfChatCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission("surf.chat.command.surf-chat")
        subcommand(SurfChatDeleteCommand("delete"))
        subcommand(SurfChatChatClearCommand("clear"))
        subcommand(SurfChatSaveCommand("saveUsers"))
        subcommand(SurfChatPlayerChatLimitCommand("setPlayerChatLimit"))
        subcommand(SurfChatCooldownCommand("setChatCooldown"))
        subcommand(SurfChatPrivateMessageCooldownCommand("setDMChatCooldown"))
        subcommand(SurfChatMessageLimitTimeCommand("setMessageLimitSpan"))
        subcommand(SurfChatMessageLimitCommand("setMessageLimit"))
    }
}
