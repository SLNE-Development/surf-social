package dev.slne.surf.social.chat.listener

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.social.chat.SurfChat
import dev.slne.surf.social.chat.external.BasicPunishApi
import dev.slne.surf.social.chat.history.MessageType
import dev.slne.surf.social.chat.history.SentMessage
import dev.slne.surf.social.chat.`object`.Channel
import dev.slne.surf.social.chat.`object`.ChatUser
import dev.slne.surf.social.chat.service.ChatFilterService
import dev.slne.surf.social.chat.service.DatabaseService
import dev.slne.surf.social.chat.util.Components
import dev.slne.surf.social.chat.util.MessageBuilder
import dev.slne.surf.surfapi.bukkit.api.SurfBukkitApi
import dev.slne.surf.surfapi.core.api.SurfCoreApi
import dev.slne.surf.surfapi.core.api.util.logger
import dev.slne.surf.surfapi.core.api.util.random

import io.papermc.paper.event.player.AsyncChatEvent
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

import me.clip.placeholderapi.PlaceholderAPI
import net.kyori.adventure.audience.Audience

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.entity.Player

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.scheduler.BukkitRunnable
import org.gradle.internal.impldep.com.amazonaws.services.kms.AWSKMSAsyncClient
import org.jetbrains.annotations.Async
import java.util.UUID
import kotlin.reflect.jvm.internal.impl.metadata.ProtoBuf
import kotlin.reflect.jvm.internal.impl.serialization.deserialization.SuspendFunctionTypeUtilKt

class PlayerAsyncChatListener : Listener {
    @EventHandler
    fun onChat(event: AsyncChatEvent) {
        val player = event.player
        var plainMessage = PlainTextComponentSerializer.plainText().serialize(event.message())
        if (ChatFilterService.containsLink(event.message())) {
            event.isCancelled = true
            SurfChat.send(player, MessageBuilder().error("Bitte sende keine Links!"))
            saveMessage(player, plainMessage, MessageType.BLOCKED_LINK)
            return
        }

        if (ChatFilterService.containsBlocked(event.message())) {
            event.isCancelled = true
            SurfChat.send(player, MessageBuilder().error("Bitte achte auf deine Wortwahl!"))
            saveMessage(player, plainMessage, MessageType.BLOCKED_WORDS)
            return
        }

        if (ChatFilterService.isSpamming(event.player.uniqueId)) {
            event.isCancelled = true
            SurfChat.send(player, MessageBuilder().error("Mal ganz ruhig hier, spam bitte nicht!"))
            saveMessage(player, plainMessage, MessageType.BLOCKED_SPAM)
            return
        }

        if (!ChatFilterService.isValidInput(plainMessage)) {
            event.isCancelled = true
            SurfChat.send(player, MessageBuilder().error("Bitte verwende keine unerlaubten Zeichen!"))
            saveMessage(player, plainMessage, MessageType.BLOCKED_INVALID)
            return
        }

        if (BasicPunishApi.isMuted(player)) {
            SurfChat.send(player, MessageBuilder().error("Du bist gemuted und kannst nicht chatten."))
            event.isCancelled = true
            saveMessage(player, plainMessage, MessageType.BLOCKED_MUTED)
            return
        }

        val channel: Channel? = Channel.getChannel(player)
        val messageID: UUID = UUID.randomUUID()
        var found = false

        if (channel != null) {
            if (plainMessage.startsWith("@all")) {
                plainMessage = plainMessage.replaceFirst("@all".toRegex(), "").trim { it <= ' ' }
                found = true
            } else if (plainMessage.startsWith("@a")) {
                plainMessage = plainMessage.replaceFirst("@a".toRegex(), "").trim { it <= ' ' }
                found = true
            }

            if (!found) {
                event.renderer { source, _, _, viewer ->
                    Components.getDeleteComponent(viewer.toPlayer(), messageID)
                        .append(Components.getTeleportComponent(viewer.toPlayer(), source.name))
                        .append(MiniMessage.miniMessage().deserialize(PlaceholderAPI.setPlaceholders(source, "%luckperms_prefix% %player_name%")))
                        .append(Component.text(" >> "))
                        .append(Components.getChannelComponent(channel))
                        .append(Component.text(" $plainMessage"))
                }
            }
            saveMessage(player, plainMessage, MessageType.SENT)
            return
        }

        event.renderer {source, _, _, viewer ->
            Components.getDeleteComponent(viewer.toPlayer(), messageID)
                .append(Components.getTeleportComponent(viewer.toPlayer(), source.name))
                .append(MiniMessage.miniMessage().deserialize(PlaceholderAPI.setPlaceholders(source, "%luckperms_prefix% %player_name%")))
                .append(Component.text(" >> "))
                .append(Component.text(" $plainMessage"))
        }
        saveMessage(player, plainMessage, MessageType.SENT)
    }
    fun saveMessage(player:Player, message:String, type : MessageType){
        ChatUser.saveSentMessage(player.uniqueId, SentMessage(message,System.currentTimeMillis()/1000,type))
    }
    private fun saveMessage(player:Player, message:Component, type : MessageType){
        if (message is TextComponent){
            saveMessage(player, message.content(), type)
        }else{
            SurfChat.instance.componentLogger.error(Component.text("Could not cast $message to TextComponent for Serialization (dev.slne.surf.social.chat.listener.PlayerAsyncChatListener)"))
        }
    }

    private fun Audience.toPlayer(): Player? {
        if (this is Player) {
            return this
        }
        return null
    }
}