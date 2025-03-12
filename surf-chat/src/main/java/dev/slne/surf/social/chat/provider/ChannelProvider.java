package dev.slne.surf.social.chat.provider;

import dev.slne.surf.social.chat.object.Channel;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.UUID;
import lombok.Getter;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

@Getter
public class ChannelProvider {
  @Getter
  private static final ChannelProvider instance = new ChannelProvider();
  private final Object2ObjectMap<UUID, Channel> channels = new Object2ObjectOpenHashMap<>();

  public boolean exists(String name) {
    return this.channels.values().stream().filter(channel -> channel.getName().equals(name)).findFirst().orElse(null) != null;
  }

  public void handleQuit(Player player) {
    Channel channel = Channel.getChannel(player);

    if(channel == null) {
      return;
    }

    if(channel.getOwner().equals(player)) {
      channel.delete();
    }
  }
}
