package dev.slne.surf.social.chat.command

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.greedyStringArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.social.chat.SurfChat
import dev.slne.surf.social.chat.external.BasicPunishApi
import dev.slne.surf.social.chat.`object`.ChatUser
import dev.slne.surf.social.chat.send
import dev.slne.surf.social.chat.service.ChatFilterService
import dev.slne.surf.social.chat.service.ChatReplyService
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.clickSuggestsCommand
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit

class ReplyCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission("surf.chat.command.reply")
        withAliases("r")
        greedyStringArgument("message")
        playerExecutor { player, args ->
            SurfChat.instance.launch {
                val message = args.getUnchecked<String>("message") ?: return@launch
                val uuid = ChatReplyService.get(player.uniqueId)

                if (uuid == null) {
                    player.send {
                        appendPrefix()
                        error("Du hast niemanden, dem du antworten kannst.")
                    }
                    return@launch
                }

                val target = Bukkit.getPlayer(uuid)

                if (target == null) {
                    player.send {
                        appendPrefix()
                        error("Du hast niemanden, dem du antworten kannst.")
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

                val targetUser: ChatUser = ChatUser.getUser(uuid)
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

                if (user.isIgnoring(targetUser.uuid)) {
                    player.send {
                        appendPrefix()
                        error("Du ignorierst den Spieler.")
                    }
                    return@launch
                }

                if (!targetUser.isIgnoring(player.uniqueId)) {
                    val user = Bukkit.getPlayer(uuid) ?: return@launch
                    user.send {
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
                        hoverEvent(Component.text("Klicke, um " + target.name + "zu antworten.", Colors.INFO))
                    }
                }
            }
        }
    }
}