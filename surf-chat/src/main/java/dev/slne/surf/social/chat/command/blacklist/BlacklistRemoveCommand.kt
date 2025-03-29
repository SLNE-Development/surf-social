package dev.slne.surf.social.chat.command.blacklist

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.StringArgument
import dev.jorel.commandapi.arguments.TextArgument
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.argument
import dev.jorel.commandapi.kotlindsl.stringArgument
import dev.slne.surf.social.chat.SurfChat
import dev.slne.surf.social.chat.command.WordBlacklistCommand
import dev.slne.surf.social.chat.provider.WordBlacklistProvider
import dev.slne.surf.social.chat.util.MessageBuilder

class BlacklistRemoveCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        argument(TextArgument("word").includeSuggestions(ArgumentSuggestions.strings(WordBlacklistCommand.getWords())))
        anyExecutor { sender, arguments ->
            val string = arguments.getUnchecked<String>("word") ?: return@anyExecutor
            val result = WordBlacklistProvider.removePunishment(string)
            if (result) SurfChat.send(MessageBuilder().info("Wort \"$string\" von der Blacklist entfernt.").build(), sender)
            else SurfChat.send(MessageBuilder().error("Wort \"$string\" ist nicht in der Blacklist.").build(), sender)
        }
    }
}