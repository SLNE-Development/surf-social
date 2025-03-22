package dev.slne.surf.social.chat.command.config

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.integerArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.social.chat.provider.ConfigurationProvider
import dev.slne.surf.social.chat.util.MessageBuilder
import dev.slne.surf.social.chat.util.sendText

class SurfChatMessageLimitCommand(commandName: String): CommandAPICommand(commandName) {
    init {
        integerArgument("limit")
        playerExecutor { player, args ->
            val limit = args.getUnchecked<Int>("limit") ?: return@playerExecutor
            ConfigurationProvider.setMessageLimit(limit)
            player.sendText(MessageBuilder().primary("Du hast das ").info("Limit an Nachrichten in ${ConfigurationProvider.getMessageLimitCooldown()/20} Sekunden").primary(" auf ").info(limit.toString()).primary(" gesetzt."))
        }
    }
}