package dev.slne.surf.social.chat.command.config

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.integerArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.social.chat.provider.ConfigurationProvider
import dev.slne.surf.social.chat.util.MessageBuilder
import dev.slne.surf.social.chat.util.sendText

class SurfChatCooldownCommand(commandName: String): CommandAPICommand(commandName) {
    init {
        integerArgument("cooldown", min = 0)
        playerExecutor { player, args ->
            val limit = args.getUnchecked<Int>("cooldown") ?: return@playerExecutor
            ConfigurationProvider.setMessageCooldown(limit)
            player.sendText(MessageBuilder().primary("Du hast den ").info("Chat-Cooldown pro Player").primary(" auf ").info((limit/20).toString()).primary(" Sekunden gesetzt."))
        }
    }
}