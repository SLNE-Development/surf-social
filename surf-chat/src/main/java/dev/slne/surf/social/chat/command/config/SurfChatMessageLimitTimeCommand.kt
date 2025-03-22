package dev.slne.surf.social.chat.command.config

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.integerArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.social.chat.provider.ConfigurationProvider
import dev.slne.surf.social.chat.util.MessageBuilder
import dev.slne.surf.social.chat.util.sendText

class SurfChatMessageLimitTimeCommand(commandName: String): CommandAPICommand(commandName) {
    init {
        integerArgument("time_span")
        playerExecutor { player, args ->
            val limit = args.getUnchecked<Int>("time_span") ?: return@playerExecutor
            ConfigurationProvider.setMessageLimitCooldown(limit)
            player.sendText(MessageBuilder().primary("Du hast das ").info("Limit auf max. ${ConfigurationProvider.getMessageLimit()} Nachrichten").primary(" in ").info((limit/20).toString()).primary(" Sekunden gesetzt."))
        }
    }
}