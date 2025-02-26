package dev.slne.surf.social.chat.command

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.subcommand

class SurfChatCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission("surf.chat.command.surf-chat")
        subcommand(SurfChatDeleteCommand("delete"))
        subcommand(SurfChatChatClearCommand("clear"))
        subcommand(SurfChatSaveCommand("saveUsers"))
    }
}
