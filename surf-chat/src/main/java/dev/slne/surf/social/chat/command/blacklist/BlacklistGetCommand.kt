package dev.slne.surf.social.chat.command.blacklist

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.TextArgument
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.argument
import dev.jorel.commandapi.kotlindsl.stringArgument
import dev.jorel.commandapi.kotlindsl.textArgument
import dev.slne.surf.social.chat.SurfChat
import dev.slne.surf.social.chat.command.WordBlacklistCommand
import dev.slne.surf.social.chat.`object`.ChatPunishment
import dev.slne.surf.social.chat.provider.WordBlacklistProvider
import dev.slne.surf.social.chat.util.MessageBuilder
import org.bukkit.command.CommandSender

class BlacklistGetCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        argument(TextArgument("word").includeSuggestions(ArgumentSuggestions.strings(WordBlacklistCommand.getWords())))
        anyExecutor{ sender: CommandSender, args: CommandArguments ->
            val word = args.getUnchecked<String>("word") ?:return@anyExecutor
            val punishment = WordBlacklistProvider.getPunishment(word)
            if (punishment != null){
                SurfChat.send(MessageBuilder().info(punishment.getInfoMessage(WordBlacklistProvider.getExactBlockedWord(word)!!)).build(), sender)
            }else SurfChat.send(MessageBuilder().error("Dieses Wort ist nicht auf der Blacklist.").build(), sender)
        }
    }
}