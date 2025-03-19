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

                if (ChatFilterService.containsLink(MiniMessage.miniMessage().deserialize(message))) {
                    SurfChat.send(player, MessageBuilder().error("Bitte sende keine Links!"))
                    ChatUser.saveSentMessage(player.uniqueId, SentMessage(message,System.currentTimeMillis()/1000,MessageType.BLOCKED_LINK_DM))
                    return@launch
                }

                if (ChatFilterService.containsBlocked(MiniMessage.miniMessage().deserialize(message))) {
                    SurfChat.send(player, MessageBuilder().error("Bitte achte auf deine Wortwahl!"))
                    ChatUser.saveSentMessage(player.uniqueId, SentMessage(message,System.currentTimeMillis()/1000,MessageType.BLOCKED_WORDS_DM))
                    return@launch
                }

                if (ChatFilterService.isSpamming(player.uniqueId)) {
                    SurfChat.send(player, MessageBuilder().error("Mal ganz ruhig hier, spam bitte nicht!"))
                    ChatUser.saveSentMessage(player.uniqueId, SentMessage(message,System.currentTimeMillis()/1000,MessageType.BLOCKED_SPAM_DM))
                    return@launch
                }

                if (!ChatFilterService.isValidInput(message)) {
                    SurfChat.send(player, MessageBuilder().error("Bitte verwende keine unerlaubten Zeichen!"))
                    ChatUser.saveSentMessage(player.uniqueId, SentMessage(message,System.currentTimeMillis()/1000,MessageType.BLOCKED_INVALID_DM))
                    return@launch
                }

                if (BasicPunishApi.isMuted(player)) {
                    SurfChat.send(player, MessageBuilder().error("Du bist gemuted und kannst nicht chatten."))
                    ChatUser.saveSentMessage(player.uniqueId, SentMessage(message,System.currentTimeMillis()/1000,MessageType.BLOCKED_MUTED_DM))
                    return@launch
                }

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

                if(!targetUser.isIgnoring(player.uniqueId)) {
                    SurfChat.send(target, MessageBuilder().suggest(MessageBuilder().darkSpacer(">>").error(" PM ").darkSpacer("| ").variableValue(player.name).darkSpacer(" ->").variableValue(" Dich: ").white(message), MessageBuilder().primary("Klicke, um zu antworten."), "/msg " + player.name + " "))
                }

                SurfChat.send(player, MessageBuilder().suggest(MessageBuilder().darkSpacer(">>").error(" PM ").darkSpacer("| ").variableValue("Du").darkSpacer(" -> ").variableValue(target.name + ": ").white(message), MessageBuilder().primary("Klicke, um zu antworten."), "/msg " + target.name + " "))
                ChatUser.saveSentMessage(player.uniqueId, SentMessage(message,System.currentTimeMillis()/1000,MessageType.SENT_DM))
            }
        }
    }
}
