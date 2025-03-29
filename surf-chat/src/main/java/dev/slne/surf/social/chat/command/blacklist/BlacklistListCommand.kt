package dev.slne.surf.social.chat.command.blacklist

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.integerArgument
import dev.slne.surf.social.chat.command.WordBlacklistCommand
import dev.slne.surf.social.chat.provider.WordBlacklistProvider
import dev.slne.surf.social.chat.util.PageableMessageBuilder
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent

class BlacklistListCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        integerArgument("page", 0, Int.MAX_VALUE, true)
        anyExecutor { sender, arguments ->
            val pages = PageableMessageBuilder().setPageCommand("/blacklist list %page%");
            for (word in WordBlacklistProvider.getWords()){
                pages.addLine(Component.text(word).clickEvent(ClickEvent.runCommand("/blacklist get $word")).hoverEvent(Component.text("Click for more information")))
            }
            val page = arguments.getUnchecked<Int>("page") ?: 1
            pages.send(sender, page)
        }
    }
}