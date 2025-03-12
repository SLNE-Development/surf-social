package dev.slne.surf.social.chat.listener;

import dev.slne.surf.social.chat.provider.ChannelProvider;
import dev.slne.surf.social.chat.service.ChatHistoryService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {
  @EventHandler
  public void onQuit(PlayerQuitEvent event) {
    ChatHistoryService.getInstance().clearInternalChatHistory(event.getPlayer().getUniqueId());
    ChannelProvider.getInstance().handleQuit(event.getPlayer());
  }
}
