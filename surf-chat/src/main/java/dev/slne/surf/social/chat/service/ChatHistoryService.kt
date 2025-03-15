package dev.slne.surf.social.chat.service

import com.github.benmanes.caffeine.cache.Caffeine
import dev.slne.surf.social.chat.`object`.HistoryPair
import dev.slne.surf.social.chat.`object`.Message
import dev.slne.surf.surfapi.bukkit.api.util.forEachPlayer
import dev.slne.surf.surfapi.core.api.util.mutableObject2ObjectMapOf
import it.unimi.dsi.fastutil.objects.Object2ObjectMap
import net.kyori.adventure.text.Component
import java.util.*

object ChatHistoryService {
    private val chatHistoryCache = Caffeine.newBuilder()
        .build<UUID, Object2ObjectMap<HistoryPair, Message>>()

    fun clearInternalChatHistory(player: UUID) {
        chatHistoryCache.invalidate(player)
    }

    fun clearChat() {
        chatHistoryCache.invalidateAll()

        forEachPlayer { player ->
            repeat(100) {
                player.sendMessage(Component.empty())
            }
        }
    }

    fun insertNewMessage(player: UUID, message: Message, messageID: UUID) {
        val chatHistory = chatHistoryCache.get(player) { mutableObject2ObjectMapOf() }

        val timestamp = System.currentTimeMillis() / 1000
        chatHistory[HistoryPair(messageID, timestamp)] = message
    }


    fun removeMessage(player: UUID, messageID: UUID) {
        val chatHistory = chatHistoryCache.getIfPresent(player) ?: return
        val key = chatHistory.keys.find { it.messageID == messageID }

        if (key != null) {
            chatHistory.remove(key)
        }
    }

    fun resend(player: UUID) {
        val chatHistory = chatHistoryCache.getIfPresent(player) ?: return

        forEachPlayer { player ->
            repeat(100) {
                player.sendMessage(Component.empty())
            }
        }

        chatHistory.object2ObjectEntrySet()
            .sortedBy { it.key.sendTime }
            .forEach { (_, value) ->
                forEachPlayer { player ->
                    player.sendMessage(value.message)
                }
            }
    }
}
