package dev.slne.surf.social.chat.listener

import dev.slne.surf.social.chat.provider.ChannelProvider
import dev.slne.surf.social.chat.service.ChatHistoryService
import dev.slne.surf.social.chat.service.ChatReplyService
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class PlayerQuitListener : Listener {
    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        ChatHistoryService.clearInternalChatHistory(event.player.uniqueId)
        ChannelProvider.handleQuit(event.player)
        ChatReplyService.clear(event.player.uniqueId)
    }
}
