package dev.slne.surf.social.chat.listener

import dev.slne.surf.social.chat.SurfChat
import dev.slne.surf.social.chat.external.BasicPunishApi
import dev.slne.surf.social.chat.`object`.Channel
import dev.slne.surf.social.chat.service.ChatFilterService
import dev.slne.surf.social.chat.util.Components
import dev.slne.surf.social.chat.util.MessageBuilder
import dev.slne.surf.surfapi.bukkit.api.SurfBukkitApi
import dev.slne.surf.surfapi.core.api.SurfCoreApi
import dev.slne.surf.surfapi.core.api.util.random

import io.papermc.paper.event.player.AsyncChatEvent

import me.clip.placeholderapi.PlaceholderAPI
import net.kyori.adventure.audience.Audience

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.entity.Player

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.util.UUID

class PlayerAsyncChatListener : Listener {
    @EventHandler
    fun onChat(event: AsyncChatEvent) {
        val player = event.player
        var plainMessage = PlainTextComponentSerializer.plainText().serialize(event.message())

        if (ChatFilterService.containsLink(event.message())) {
            event.isCancelled = true
            SurfChat.send(player, MessageBuilder().error("Bitte sende keine Links!"))
            return
        }

        if (ChatFilterService.containsBlocked(event.message())) {
            event.isCancelled = true
            SurfChat.send(player, MessageBuilder().error("Bitte achte auf deine Wortwahl!"))
            return
        }

        if (ChatFilterService.isSpamming(event.player.uniqueId)) {
            event.isCancelled = true
            SurfChat.send(player, MessageBuilder().error("Mal ganz ruhig hier, spam bitte nicht!"))
            return
        }

        if (!ChatFilterService.isValidInput(plainMessage)) {
            event.isCancelled = true
            SurfChat.send(player, MessageBuilder().error("Bitte verwende keine unerlaubten Zeichen!"))
            return
        }

        if (BasicPunishApi.isMuted(player)) {
            SurfChat.send(player, MessageBuilder().error("Du bist gemuted und kannst nicht chatten."))
            event.isCancelled = true
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
            return
        }

        event.renderer {source, _, _, viewer ->
            Components.getDeleteComponent(viewer.toPlayer(), messageID)
                .append(Components.getTeleportComponent(viewer.toPlayer(), source.name))
                .append(MiniMessage.miniMessage().deserialize(PlaceholderAPI.setPlaceholders(source, "%luckperms_prefix% %player_name%")))
                .append(Component.text(" >> "))
                .append(Component.text(" $plainMessage"))
        }
    }

    private fun Audience.toPlayer(): Player? {
        if (this is Player) {
            return this
        }
        return null
    }
}