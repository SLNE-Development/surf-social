package dev.slne.surf.social.chat.command

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.GreedyStringArgument
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.jorel.commandapi.kotlindsl.greedyStringArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.social.chat.SurfChat
import dev.slne.surf.social.chat.external.BasicPunishApi
import dev.slne.surf.social.chat.history.MessageType
import dev.slne.surf.social.chat.history.SentMessage
import dev.slne.surf.social.chat.`object`.ChatUser
import dev.slne.surf.social.chat.service.ChatFilterService
import dev.slne.surf.social.chat.service.ChatReplyService
import dev.slne.surf.social.chat.util.MessageBuilder
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit

class ReplyCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission("surf.chat.command.reply")
        withAliases("r")
        greedyStringArgument("message")
        playerExecutor{ player, args ->
            SurfChat.instance.launch {
                val message = args.getUnchecked<String>("message") ?: return@launch
                val uuid = ChatReplyService.get(player.uniqueId)

                if(uuid == null) {
                    SurfChat.send(player, MessageBuilder().error("Du hast niemanden, dem du antworten kannst."))
                    //Not saved in Chat History
                    return@launch
                }

                val target = Bukkit.getPlayer(uuid)

                if(target == null) {
                    SurfChat.send(player, MessageBuilder().error("Du hast niemanden, dem du antworten kannst."))
                    return@launch
                }

                if(target == player) {
                    SurfChat.send(player, MessageBuilder().error("Du kannst dir nicht selbst schreiben."))
                    //Not saved in Chat History
                    return@launch
                }

                val targetUser: ChatUser = ChatUser.getUser(uuid)
                val user: ChatUser = ChatUser.getUser(player.uniqueId)

                if (targetUser.toggledPM) {
                    SurfChat.send(player, MessageBuilder().error("Der Spieler hat Privatnachrichten deaktiviert."))
                    ChatUser.saveSentMessage(player.uniqueId, SentMessage(message,System.currentTimeMillis()/1000,MessageType.BLOCKED_DISABLED_DM))
                    return@launch
                }

                if(user.isIgnoring(targetUser.uuid)) {
                    SurfChat.send(player, MessageBuilder().error("Du ignorierst den Spieler."))
                    ChatUser.saveSentMessage(player.uniqueId, SentMessage(message,System.currentTimeMillis()/1000,MessageType.BLOCKED_IGNORING_DM))
                    return@launch
                }

                if(!ChatFilterService.validateCompleteMessage(player, Component.text(message),message, true)){
                    return@launch
                }

                if(!targetUser.isIgnoring(player.uniqueId)) {
                    SurfChat.send(Bukkit.getPlayer(uuid) ?: return@launch, MessageBuilder().suggest(MessageBuilder().darkSpacer(">>").error(" PM ").darkSpacer("| ").variableValue(player.name).darkSpacer(" ->").variableValue(" Dich: ").white(message), MessageBuilder().primary("Clicke, um anzuworten."), "/msg " + player.name + " "))
                }

                SurfChat.send(player, MessageBuilder().suggest(MessageBuilder().darkSpacer(">>").error(" PM ").darkSpacer("| ").variableValue("Du").darkSpacer(" -> ").variableValue(target.name + ": ").white(message), MessageBuilder().primary("Clicke, um anzuworten."), "/msg " + target.name + " "))
                ChatUser.saveSentMessage(player.uniqueId, SentMessage(message,System.currentTimeMillis()/1000,MessageType.SENT_DM))

            }
        }
    }
}