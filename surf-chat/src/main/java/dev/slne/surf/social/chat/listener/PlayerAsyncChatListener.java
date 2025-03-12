package dev.slne.surf.social.chat.listener;
import dev.slne.surf.social.chat.external.BasicPunishApi;
import dev.slne.surf.social.chat.object.Channel;
import dev.slne.surf.social.chat.object.Message;
import dev.slne.surf.social.chat.provider.ConfigProvider;
import dev.slne.surf.social.chat.service.ChatFilterService;
import dev.slne.surf.social.chat.service.ChatHistoryService;

import dev.slne.surf.social.chat.util.Colors;
import dev.slne.surf.social.chat.util.PluginColor;
import io.papermc.paper.event.player.AsyncChatEvent;

import java.security.SecureRandom;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@Getter
public class PlayerAsyncChatListener implements Listener {
  private final SecureRandom random = new SecureRandom();
  private final String deletePerms = "surf.chat.delete";
  private final String teleportPerms = "surf.chat.teleport";

  @EventHandler
  public void onChat(AsyncChatEvent event) {
    Player player = event.getPlayer();
    String plainMessage = PlainTextComponentSerializer.plainText().serialize(event.message());

    if(event.isCancelled()) {
      return;
    }

    if(ChatFilterService.getInstance().containsLink(event.message())) {
      event.setCancelled(true);
      player.sendMessage(Colors.PREFIX.append(Component.text("Bitte sende keine Links!", NamedTextColor.RED)));
      return;
    }

    if(ChatFilterService.getInstance().containsBlocked(event.message())) {
      event.setCancelled(true);
      player.sendMessage(Colors.PREFIX.append(Component.text("Bitte achte auf deine Wortwahl!", NamedTextColor.RED)));
      return;
    }

    if(ChatFilterService.getInstance().isSpamming(event.getPlayer().getUniqueId())) {
      event.setCancelled(true);
      player.sendMessage(Colors.PREFIX.append(Component.text("Mal ganz ruhig hier, spam bitte nicht!", NamedTextColor.RED)));
      return;
    }

    if(!ChatFilterService.getInstance().isValidInput(plainMessage)) {
      event.setCancelled(true);
      player.sendMessage(Colors.PREFIX.append(Component.text("Bitte verwende keine unerlaubten Zeichen!", NamedTextColor.RED)));
      return;
    }

    if(BasicPunishApi.isMuted(player)) {
      event.setCancelled(true);
      return;
    }

    event.setCancelled(true);

    Channel channel = Channel.getChannel(player);
    int messageID = this.getRandomID();
    boolean found = false;

    if (channel != null) {
      if (plainMessage.startsWith("@all")) {
        plainMessage = plainMessage.replaceFirst("@all", "").trim();
        found = true;
      } else if (plainMessage.startsWith("@a")) {
        plainMessage = plainMessage.replaceFirst("@a", "").trim();
        found = true;
      }

      if(!found) {
        for (Player onlinePlayer : channel.getOnlinePlayers()) {
          Component message = MiniMessage.miniMessage().deserialize(PlaceholderAPI.setPlaceholders(player, ConfigProvider.getInstance().getPublicMessageFormat()))
              .replaceText(TextReplacementConfig.builder()
                  .matchLiteral("%message%")
                  .replacement(Component.text(plainMessage))
                  .build())
              .replaceText(TextReplacementConfig.builder()
                  .matchLiteral("%channel%")
                  .replacement(" " + channel.getName() + " ")
                  .build())
              .replaceText(TextReplacementConfig.builder()
                  .matchLiteral("%delete%")
                  .replacement(onlinePlayer.hasPermission(this.deletePerms) ? this.getDeleteComponent(messageID) : Component.empty())
                  .build())
              .replaceText(TextReplacementConfig.builder()
                  .matchLiteral("%teleport%")
                  .replacement(onlinePlayer.hasPermission(this.teleportPerms) ? this.getTeleportComponent(player.getName()) : Component.empty())
                  .build());

          onlinePlayer.sendMessage(message);
          ChatHistoryService.getInstance().insertNewMessage(onlinePlayer.getUniqueId(), Message.builder()
              .receiver(onlinePlayer.getName())
              .sender(player.getName())
              .message(message)
              .build(), messageID);
        }
      }

      return;
    }

    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
      Component message = MiniMessage.miniMessage().deserialize(PlaceholderAPI.setPlaceholders(player, ConfigProvider.getInstance().getPublicMessageFormat()))
          .replaceText(TextReplacementConfig.builder()
              .matchLiteral("%message%")
              .replacement(event.message())
              .build())
          .replaceText(TextReplacementConfig.builder()
              .matchLiteral("%channel%")
              .replacement("")
              .build())
          .replaceText(TextReplacementConfig.builder()
              .matchLiteral("%delete%")
              .replacement(onlinePlayer.hasPermission(this.deletePerms) ? this.getDeleteComponent(messageID) : Component.empty())
              .build())
          .replaceText(TextReplacementConfig.builder()
              .matchLiteral("%teleport%")
              .replacement(onlinePlayer.hasPermission(this.teleportPerms) ? this.getTeleportComponent(player.getName()) : Component.empty())
              .build())
          ;

      onlinePlayer.sendMessage(message);
      ChatHistoryService.getInstance().insertNewMessage(onlinePlayer.getUniqueId(), Message.builder()
          .receiver(onlinePlayer.getName())
          .sender(player.getName())
          .message(message)
          .build(), messageID);
    }
  }

  private Component getDeleteComponent(int id) {
    return Component.text("[", Colors.DARK_SPACER).append(Component.text("DEL", Colors.VARIABLE_KEY)).append(Component.text("]", Colors.DARK_SPACER))
        .clickEvent(ClickEvent.runCommand("/surfchat delete " + id))
        .hoverEvent(Component.text("Nachricht löschen", PluginColor.RED));
  }

  private Component getTeleportComponent(String name) {
    return Component.text("[", Colors.DARK_SPACER).append(Component.text("TP", Colors.VARIABLE_KEY)).append(Component.text("]", Colors.DARK_SPACER))
        .clickEvent(ClickEvent.runCommand("/tp " + name))
        .hoverEvent(Component.text("Zum Spieler teleportieren", PluginColor.BLUE_MID));
  }

  private int getRandomID() {
    return this.random.nextInt(1000000);
  }
}
