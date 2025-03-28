package dev.slne.surf.social.chat.service

import dev.slne.surf.social.chat.SurfChat
import dev.slne.surf.social.chat.external.BasicPunishApi
import dev.slne.surf.social.chat.history.MessageType
import dev.slne.surf.social.chat.history.SentMessage
import dev.slne.surf.social.chat.`object`.ChatUser
import dev.slne.surf.social.chat.util.MessageBuilder
import dev.slne.surf.social.chat.listener.PlayerAsyncChatListener
import dev.slne.surf.social.chat.util.sendText
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.text
import dev.slne.surf.surfapi.core.api.util.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextReplacementConfig
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Material
import org.bukkit.entity.Player
import java.util.*
import kotlin.io.path.*
import kotlin.system.measureTimeMillis
import kotlin.time.Duration.Companion.seconds



object ChatFilterService {
    
    private val allowedDomains = mutableObjectSetOf<String>()

    private val validCharactersRegex = "^[a-zA-Z0-9/.:_,()%&=?!<>|#^\"²³+*~-äöü@ ]*$".toRegex()
    private val urlRegex =
        "((http|https|ftp)://)?([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?".toRegex(RegexOption.IGNORE_CASE)


    fun containsBlocked(message: String):ChatPunishment?{
        return WordBlacklistProvider.getPunishment(message)
    }


    fun containsLink(message: Component): Boolean {
        val plainMessage = PlainTextComponentSerializer.plainText().serialize(message)

        return urlRegex.findAll(plainMessage).any { result ->
            val domain = result.groupValues.getOrNull(3) ?: return@any false
            allowedDomains.none { domain.endsWith(it) }
        }
    }

    fun isValidInput(input: String): Boolean {
        return validCharactersRegex.matches(input)
    }
    fun validateCompleteMessage(player:Player, message:Component,plainMessage:String, isDM:Boolean):Boolean{
        when {
            containsLink(message) -> {
                SurfChat.send(player, MessageBuilder().error("Bitte sende keine Links!"))
                saveMessage(player, plainMessage, MessageType.Builder.getType(MessageType.BLOCKED_LINK, isDM))
                return false
            }
            containsBlocked(message) -> {
                SurfChat.send(player, MessageBuilder().error("Bitte achte auf deine Wortwahl!"))
                saveMessage(player, plainMessage, MessageType.Builder.getType(MessageType.BLOCKED_WORDS, isDM))
                return false
            }
            !isValidInput(plainMessage) -> {
                SurfChat.send(player, MessageBuilder().error("Bitte verwende keine unerlaubten Zeichen!"))
                saveMessage(player, plainMessage, MessageType.Builder.getType(MessageType.BLOCKED_INVALID, isDM))
                return false
            }
            BasicPunishApi.isMuted(player) -> {
                SurfChat.send(player, MessageBuilder().error("Du bist gemuted und kannst nicht chatten."))
                saveMessage(player, plainMessage, MessageType.Builder.getType(MessageType.BLOCKED_MUTED, isDM))
                return false
            }
            !ChatLimitService.validateMessage(player, plainMessage, isDM) -> {
                return false
            }
            else -> return true
        }
    }

    fun saveMessage(player:Player, message:String, type : MessageType){
        ChatUser.saveSentMessage(player.uniqueId, SentMessage(message,System.currentTimeMillis()/1000,type))
    }
}
