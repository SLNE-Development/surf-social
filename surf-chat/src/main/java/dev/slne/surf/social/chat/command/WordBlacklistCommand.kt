package dev.slne.surf.social.chat.command

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.GreedyStringArgument
import dev.jorel.commandapi.arguments.IntegerArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.jorel.commandapi.arguments.TextArgument
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.social.chat.command.blacklist.*
import dev.slne.surf.social.chat.command.config.*
import dev.slne.surf.social.chat.permission.SurfChatPermissions
import dev.slne.surf.social.chat.provider.WordBlacklistProvider

class WordBlacklistCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(SurfChatPermissions.blacklistCommand)
        subcommand(BlacklistAddCommand("add"))
        subcommand(BlacklistListCommand("list"))
        subcommand(BlacklistRemoveCommand("remove"))
        subcommand(BlacklistGetCommand("get"))
        subcommand(BlacklistDefaultCommand("default"))

    }

    companion object{
        val punishment = StringArgument("punishment")
            .setOptional(true)
            .includeSuggestions(ArgumentSuggestions.strings("MUTE", "BLOCK", "BAN", "KICK"))
        val duration = StringArgument("duration")
            .setOptional(true)
            .includeSuggestions(ArgumentSuggestions.strings("null", "1d", "1h", "1m", "2d12h"))
        val message = TextArgument("message")
            .setOptional(true)
            .includeSuggestions(ArgumentSuggestions.strings("\"Komm wieder in %time\""))
        fun getWords():List<String>{
            val list = ArrayList<String>()
            WordBlacklistProvider.getWords().forEach {
                if (it.contains(" ")||it.contains("[äöü!?*]".toRegex())){
                    list.add("\"$it\"")
                }else list.add(it)
            }
            return list
        }
    }
}