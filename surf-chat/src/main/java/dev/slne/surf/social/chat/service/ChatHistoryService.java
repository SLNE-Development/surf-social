package dev.slne.surf.social.chat.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import dev.slne.surf.social.chat.object.HistoryPair;
import dev.slne.surf.social.chat.object.Message;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Comparator;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.jspecify.annotations.Nullable;

public class ChatHistoryService {
  @Getter
  private static final ChatHistoryService instance = new ChatHistoryService();
  private final Cache<UUID, Object2ObjectMap<HistoryPair, Message>> chatHistoryCache = Caffeine
      .newBuilder()
      .build();

  public void clearInternalChatHistory(UUID player) {
    chatHistoryCache.invalidate(player);
  }

  public void clearChat() {
    this.chatHistoryCache.invalidateAll();

    Bukkit.getOnlinePlayers().forEach(player -> {
      int index = 0;
      while (index < 100) {
        player.sendMessage(Component.empty());
        index++;
      }
    });
  }

  public void insertNewMessage(UUID player, Message message, int messageID) {
    Object2ObjectMap<HistoryPair, Message> chatHistory = chatHistoryCache.getIfPresent(player);

      if (chatHistory == null) {
          chatHistory = new Object2ObjectOpenHashMap<>();
          chatHistoryCache.put(player, chatHistory);
      }

      int timestamp = (int) (System.currentTimeMillis() / 1000);
      chatHistory.put(new HistoryPair(messageID, timestamp), message);
  }


  public void removeMessage(UUID player, int messageID) {
    Object2ObjectMap<HistoryPair, Message> chatHistory = chatHistoryCache.getIfPresent(player);

    if (chatHistory != null) {
      chatHistory.entrySet().removeIf(entry -> entry.getKey().getMessageID() == messageID);
    }
  }

  public void resend(UUID player) {
      Object2ObjectMap<HistoryPair, Message> chatHistory = chatHistoryCache.getIfPresent(player);

      Bukkit.getOnlinePlayers().forEach(online -> {
          int index = 0;
          while (index < 100) {
              online.sendMessage(Component.empty());
              index++;
          }
      });

      if (chatHistory != null) {
          chatHistory.object2ObjectEntrySet()
              .stream()
              .sorted(Comparator.comparingLong(entry -> entry.getKey().getSendTime()))
              .forEach(entry -> {
                  Bukkit.getOnlinePlayers().forEach(target -> target.sendMessage(entry.getValue().message()));
              });
      }
  }
}
