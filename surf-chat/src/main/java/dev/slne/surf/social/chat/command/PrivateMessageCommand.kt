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
import dev.slne.surf.social.chat.history.MessageType
import dev.slne.surf.social.chat.history.SentMessage
import dev.slne.surf.social.chat.`object`.ChatUser
import dev.slne.surf.social.chat.service.ChatFilterService
import dev.slne.surf.social.chat.util.MessageBuilder
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.entity.Player

class PrivateMessageCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        playerArgument("player")
        greedyStringArgument("message")

        withAliases("tell", "w", "pm", "dm")
        withPermission("surf.chat.command.private-message")
        playerExecutor {player, args ->
            SurfChat.instance.launch {
                val target = args.getUnchecked<Player>("player") ?: return@launch
                val message = args.getUnchecked<String>("message") ?: return@launch


                val targetUser: ChatUser = ChatUser.getUser(target.uniqueId)
                val user: ChatUser = ChatUser.getUser(player.uniqueId)


                if (targetUser.toggledPM) {
                    SurfChat.send(player, MessageBuilder().error("Der Spieler hat Privatnachrichten deaktiviert."))
                    ChatUser.saveSentMessage(player.uniqueId, SentMessage(message,System.currentTimeMillis()/1000,MessageType.BLOCKED_DISABLED_DM))
                    return@launch
                }

                if(user.isIgnoring(target.uniqueId)) {
                    SurfChat.send(player, MessageBuilder().error("Du ignorierst den Spieler."))
                    ChatUser.saveSentMessage(player.uniqueId, SentMessage(message,System.currentTimeMillis()/1000,MessageType.BLOCKED_IGNORING_DM))
                    return@launch
                }

                if(target == player) {
                    SurfChat.send(player, MessageBuilder().error("Du kannst dir nicht selbst schreiben."))
                    //Not saved in message history
                    return@launch
                }

                if(!ChatFilterService.validateCompleteMessage(player, Component.text(message),message, true)){
                    return@launch
                }

                if(!targetUser.isIgnoring(player.uniqueId)) {
                    SurfChat.send(target, MessageBuilder().suggest(MessageBuilder().darkSpacer(">>").error(" PM ").darkSpacer("| ").variableValue(player.name).darkSpacer(" ->").variableValue(" Dich: ").white(message), MessageBuilder().primary("Klicke, um zu antworten."), "/msg " + player.name + " "))
                }

                SurfChat.send(player, MessageBuilder().suggest(MessageBuilder().darkSpacer(">>").error(" PM ").darkSpacer("| ").variableValue("Du").darkSpacer(" -> ").variableValue(target.name + ": ").white(message), MessageBuilder().primary("Klicke, um zu antworten."), "/msg " + target.name + " "))
                ChatUser.saveSentMessage(player.uniqueId, SentMessage(message,System.currentTimeMillis()/1000,MessageType.SENT_DM))
            }
        }
    }
}
