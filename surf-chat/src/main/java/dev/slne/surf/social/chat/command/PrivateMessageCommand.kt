package dev.slne.surf.social.chat.command

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.GreedyStringArgument
import dev.jorel.commandapi.arguments.PlayerArgument
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.jorel.commandapi.kotlindsl.greedyStringArgument
import dev.jorel.commandapi.kotlindsl.playerArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.social.chat.SurfChat
import dev.slne.surf.social.chat.external.BasicPunishApi
import dev.slne.surf.social.chat.`object`.ChatUser
import dev.slne.surf.social.chat.send
import dev.slne.surf.social.chat.service.ChatFilterService
import dev.slne.surf.social.chat.util.MessageBuilder
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.clickSuggestsCommand
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.entity.Player

class PrivateMessageCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        playerArgument("player")
        greedyStringArgument("message")

        withAliases("tell", "w", "pm", "dm")
        withPermission("surf.chat.command.private-message")
        playerExecutor { player, args ->
            SurfChat.instance.launch {
                val target = args.getUnchecked<Player>("player") ?: return@launch
                val message = args.getUnchecked<String>("message") ?: return@launch

                val targetUser: ChatUser = ChatUser.getUser(target.uniqueId)
                val user: ChatUser = ChatUser.getUser(player.uniqueId)

                if (ChatFilterService.containsLink(MiniMessage.miniMessage().deserialize(message))) {
                    player.send {
                        appendPrefix()
                        error("Bitte sende keine Links!")
                    }
                    return@launch
                }

                if (ChatFilterService.containsBlocked(MiniMessage.miniMessage().deserialize(message))) {
                    player.send {
                        appendPrefix()
                        error("Bitte achte auf deine Wortwahl!")
                    }
                    return@launch
                }

                if (ChatFilterService.isSpamming(player.uniqueId)) {
                    player.send {
                        appendPrefix()
                        error("Mal ganz ruhig hier, spam bitte nicht!")
                    }
                    return@launch
                }

                if (!ChatFilterService.isValidInput(message)) {
                    player.send {
                        appendPrefix()
                        error("Bitte verwende keine unerlaubten Zeichen!")
                    }
                    return@launch
                }

                if (BasicPunishApi.isMuted(player)) {
                    player.send {
                        appendPrefix()
                        error("Du bist stummgeschaltet und kannst nicht schreiben.")
                    }
                    return@launch
                }

                if (targetUser.toggledPM) {
                    player.send {
                        appendPrefix()
                        error("Der Spieler hat Privatnachrichten deaktiviert.")
                    }
                    return@launch
                }

                if (user.isIgnoring(target.uniqueId)) {
                    player.send {
                        appendPrefix()
                        error("Du ignorierst den Spieler.")
                    }
                    return@launch
                }

                if (target == player) {
                    player.send {
                        appendPrefix()
                        error("Du kannst dir nicht selbst schreiben.")
                    }
                    return@launch
                }

                if (!targetUser.isIgnoring(player.uniqueId)) {
                    target.send {
                        appendPrefix()
                        darkSpacer(">> ")
                        error("PM")
                        darkSpacer(" | ")
                        variableValue(player.name).darkSpacer("->")
                        variableValue(" Dir » ")
                        append(Component.text(message, Colors.WHITE))
                        append {
                            clickSuggestsCommand("/msg " + player.name + " ")
                            hoverEvent(Component.text("Klicke, um " + player.name + "zu antworten.", Colors.INFO))
                        }

                    }
                }
                player.send {
                    appendPrefix()
                    darkSpacer(">> ")
                    error("PM")
                    darkSpacer(" | ")
                    variableValue(" Du ->")
                    variableValue(player.name).darkSpacer(" » ")
                    append(Component.text(message, Colors.WHITE))
                    append {
                        clickSuggestsCommand("/msg " + target.name + " ")
                        hoverEvent(Component.text("Klicke, um " + player.name + "zu antworten.", Colors.INFO))
                    }
                }
            }
        }
    }
}
