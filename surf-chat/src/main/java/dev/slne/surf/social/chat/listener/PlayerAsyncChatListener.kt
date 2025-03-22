package dev.slne.surf.social.chat.listener

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.social.chat.SurfChat
import dev.slne.surf.social.chat.external.BasicPunishApi
import dev.slne.surf.social.chat.history.MessageType
import dev.slne.surf.social.chat.history.SentMessage
import dev.slne.surf.social.chat.`object`.Channel
import dev.slne.surf.social.chat.`object`.ChatUser
import dev.slne.surf.social.chat.permission.SurfChatPermissions
import dev.slne.surf.social.chat.provider.ConfigurationProvider
import dev.slne.surf.social.chat.service.ChatFilterService
import dev.slne.surf.social.chat.service.ChatLimitService
import dev.slne.surf.social.chat.service.DatabaseService
import dev.slne.surf.social.chat.util.Components
import dev.slne.surf.social.chat.util.MessageBuilder
import dev.slne.surf.social.chat.util.Permission
import dev.slne.surf.social.chat.util.sendText
import dev.slne.surf.surfapi.bukkit.api.SurfBukkitApi
import dev.slne.surf.surfapi.core.api.SurfCoreApi
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.text
import dev.slne.surf.surfapi.core.api.util.logger
import dev.slne.surf.surfapi.core.api.util.random

import io.papermc.paper.event.player.AsyncChatEvent
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

import me.clip.placeholderapi.PlaceholderAPI
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.key.Key

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextReplacementConfig
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.scheduler.BukkitRunnable
import org.gradle.internal.impldep.com.amazonaws.services.kms.AWSKMSAsyncClient
import org.jetbrains.annotations.Async
import java.util.UUID
import java.util.regex.Pattern
import kotlin.reflect.jvm.internal.impl.metadata.ProtoBuf
import kotlin.reflect.jvm.internal.impl.serialization.deserialization.SuspendFunctionTypeUtilKt

class PlayerAsyncChatListener : Listener {
    @EventHandler
    fun onChat(event: AsyncChatEvent) {
        val player = event.player
        var plainMessage = PlainTextComponentSerializer.plainText().serialize(event.message().parseItemPlaceholder(player))

        if(!ChatFilterService.validateCompleteMessage(player, event.message(),plainMessage, false)){
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
            ChatFilterService.saveMessage(player, plainMessage, MessageType.SENT)
            return
        }

        event.renderer { source, _, _, viewer ->
            Components.getDeleteComponent(viewer.toPlayer(), messageID)
                .append(Components.getTeleportComponent(viewer.toPlayer(), source.name))
                .append(MiniMessage.miniMessage().deserialize(PlaceholderAPI.setPlaceholders(source, "%luckperms_prefix% %player_name%")))
                .append(Component.text(" >> "))
                .append(Component.text(" $plainMessage"))
        }
        ChatFilterService.saveMessage(player, plainMessage, MessageType.SENT)
    }

    private fun Audience.toPlayer(): Player? {
        if (this is Player) {
            return this
        }
        return null
    }

    private fun Component.parseItemPlaceholder(player: Player): Component {
        val stack = player.inventory.itemInMainHand
        if (!PlainTextComponentSerializer.plainText().serialize(this).contains("[item]")) return this

        if (stack.type == Material.AIR) {
            player.sendText(MessageBuilder().error("Du hast kein Item in der Hand!"))
            return this
        }

        return this.replaceText(TextReplacementConfig.builder()
            .match(Pattern.quote("[item]"))
            .replacement(when {
                stack.amount > 1 -> text("${stack.amount}x ", Colors.VARIABLE_VALUE).append(Component.translatable(stack.translationKey()))
                else -> Component.translatable(stack.translationKey())
            })
            .build()
        )
    }

}