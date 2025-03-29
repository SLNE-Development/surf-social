package dev.slne.surf.social.chat.service

import dev.slne.surf.social.chat.SurfChat
import dev.slne.surf.social.chat.external.BasicPunishApi
import dev.slne.surf.social.chat.history.MessageType
import dev.slne.surf.social.chat.history.SentMessage
import dev.slne.surf.social.chat.`object`.ChatUser
import dev.slne.surf.social.chat.util.MessageBuilder
import dev.slne.surf.social.chat.`object`.ChatPunishment
import dev.slne.surf.social.chat.permission.SurfChatPermissions
import dev.slne.surf.social.chat.provider.WordBlacklistProvider
import dev.slne.surf.surfapi.bukkit.api.util.forEachPlayer
import dev.slne.surf.surfapi.core.api.util.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.entity.Player


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
        val cp = if(!player.hasPermission(SurfChatPermissions.bypassBlacklist)) containsBlocked(plainMessage) else null
        when {
            (cp != null) -> {
                saveMessage(player, plainMessage, MessageType.Builder.getType(MessageType.BLOCKED_WORDS, isDM))
                handlePunishment(player, cp, plainMessage)
                return false
            }
            containsLink(message) -> {
                SurfChat.send(player, MessageBuilder().error("Bitte sende keine Links!"))
                saveMessage(player, plainMessage, MessageType.Builder.getType(MessageType.BLOCKED_LINK, isDM))
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

    fun handlePunishment(player:Player, punishment: ChatPunishment, message: String){
        SurfChat.send(player, MessageBuilder().error(punishment.getMessage()))
        val duration = if (punishment.getTimeText().isNotBlank()) " Dauer: " + punishment.getTimeText() else ""
        val msg = MessageBuilder().info(player.name + " hat versucht, \'").error(message)
            .info("\' zu schreiben. Automatische Bestrafung: ")
            .secondary(punishment.getPunishment().text).info(duration).build()
        forEachPlayer {
            if (it.hasPermission(SurfChatPermissions.getPunishmentNotification))
                SurfChat.send(it, msg)
        }
        when (punishment.getPunishment()){
            ChatPunishment.Punishment.BAN->{} //TODO() Duration can be requested via punishment.getTime()
            ChatPunishment.Punishment.BLOCK ->{} //TODO()
            ChatPunishment.Punishment.MUTE ->{} //TODO() Duration can be requested via punishment.getTime()
            ChatPunishment.Punishment.KICK ->{} //TODO()
        }
    }
}
