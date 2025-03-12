package dev.slne.surf.social.chat;

import dev.jorel.commandapi.CommandAPI;
import dev.slne.surf.social.chat.command.PrivateMessageCommand;
import dev.slne.surf.social.chat.command.SurfChatCommand;
import dev.slne.surf.social.chat.command.channel.ChannelCommand;
import dev.slne.surf.social.chat.listener.PlayerAsyncChatListener;
import dev.slne.surf.social.chat.listener.PlayerQuitListener;
import dev.slne.surf.social.chat.provider.ConfigProvider;
import dev.slne.surf.social.chat.service.ChatFilterService;

import dev.slne.surf.social.chat.util.Colors;
import dev.slne.surf.social.chat.util.MessageBuilder;
import net.kyori.adventure.text.Component;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

public class SurfChat extends JavaPlugin {
  @Override
  public void onLoad() {
    ChatFilterService.getInstance().loadBlockedWords();
  }

  @Override
  public void onEnable() {
    CommandAPI.unregister("msg");
    CommandAPI.unregister("tell");
    CommandAPI.unregister("w");


    new PrivateMessageCommand("msg").register();
    new ChannelCommand("channel").register();
    new SurfChatCommand("surfchat").register();

    this.saveDefaultConfig();

    ConfigProvider.getInstance().reload();

    Bukkit.getPluginManager().registerEvents(new PlayerAsyncChatListener(), this);
    Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(), this);
  }

  public static SurfChat getInstance() {
    return getPlugin(SurfChat.class);
  }
  public static void sendMessage(OfflinePlayer player, Component text) {
    if(player.isOnline()) {
      player.getPlayer().sendMessage(Colors.PREFIX.append(text));
    }
  }

  public static void message(OfflinePlayer player, MessageBuilder text) {
    if(player.isOnline()) {
      player.getPlayer().sendMessage(Colors.PREFIX.append(text.build()));
    }
  }
}
