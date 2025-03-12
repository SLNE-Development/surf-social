package dev.slne.surf.social.chat.object;

import dev.slne.surf.social.chat.SurfChat;
import dev.slne.surf.social.chat.provider.ChannelProvider;
import dev.slne.surf.social.chat.util.MessageBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Getter
@Setter
@Builder
public class Channel {
  @Builder.Default
  private OfflinePlayer owner = null;
  @Builder.Default
  private ObjectSet<OfflinePlayer> members = new ObjectArraySet<>();
  @Builder.Default
  private ObjectSet<OfflinePlayer> moderators = new ObjectArraySet<>();
  @Builder.Default
  private ObjectSet<OfflinePlayer> bannedPlayers = new ObjectArraySet<>();
  @Builder.Default
  private ObjectSet<OfflinePlayer> invites = new ObjectArraySet<>();

  @Builder.Default
  private String name = UUID.randomUUID().toString();

  @Builder.Default
  private String description = "Ein cooler Kanal!";

  @Builder.Default
  private boolean closed = true;

  public boolean isMember(OfflinePlayer player) {
    return members.contains(player);
  }

  public boolean isMember(CommandSender player) {
    if(player instanceof OfflinePlayer sender) {
      return members.contains(sender);
    }

    return false;
  }

  public boolean isOwnerO(OfflinePlayer player) {
    return owner.equals(player);
  }

  public boolean isOwner(CommandSender sender) {
    return owner.equals(sender);
  }

  public boolean hasInvite(CommandSender sender) {
    if(sender instanceof OfflinePlayer player) {
      return invites.contains(player);
    }

    return false;
  }

  public boolean isModerator(CommandSender player) {
    if(player instanceof OfflinePlayer sender) {
      return moderators.contains(sender);
    }

    return false;
  }

  public boolean isModeratorO(OfflinePlayer player) {
    return moderators.contains(player);
  }

  public void invite(OfflinePlayer player) {
    if(members.contains(player)) {
      return;
    }

    if(moderators.contains(player)) {
      return;
    }

    if(owner.equals(player)) {
      return;
    }

    if(bannedPlayers.contains(player)) {
      return;
    }

    if(invites.contains(player)) {
      return;
    }

    invites.add(player);
  }

  public void ban(OfflinePlayer player) {
    if(!members.contains(player)) {
      return;
    }

    if(owner.equals(player)) {
      return;
    }

    if(bannedPlayers.contains(player)) {
      return;
    }

    bannedPlayers.add(player);
    this.leave(player);
  }

  public void unban(OfflinePlayer player) {
    if(!bannedPlayers.contains(player)) {
      return;
    }

    bannedPlayers.remove(player);
  }

  public ObjectSet<Player> getOnlinePlayers() {
    final ObjectSet<Player> players = new ObjectArraySet<>();

    players.addAll(members.stream().filter(OfflinePlayer::isOnline).map(OfflinePlayer::getPlayer).collect(Collectors.toCollection(ObjectArraySet::new)));
    players.addAll(moderators.stream().filter(OfflinePlayer::isOnline).map(OfflinePlayer::getPlayer).collect(Collectors.toCollection(ObjectArraySet::new)));

    if(this.getOwner().isOnline()) {
      players.add(this.getOwner().getPlayer());
    }

    return players;
  }

  public void promote(OfflinePlayer player) {
    if(!members.contains(player)) {
      return;
    }

    if(owner.equals(player)) {
      return;
    }

    if(moderators.contains(player)) {
      return;
    }

    members.remove(player);
    moderators.add(player);
  }

  public void demote(OfflinePlayer player) {
    if(!moderators.contains(player)) {
      return;
    }

    moderators.remove(player);
  }

  public void kick(OfflinePlayer player) {
    if(!members.contains(player)) {
      return;
    }

    if(owner.equals(player)) {
      return;
    }

    this.leave(player);
  }

  public void acceptInvite(OfflinePlayer player) {
    if(!invites.contains(player)) {
      return;
    }

    Channel channel = Channel.getChannelO(player);

    if(channel != null) {
      channel.leave(player);
    }

    this.join(player);
  }

  public void revokeInvite(OfflinePlayer player) {
    if(!invites.contains(player)) {
      return;
    }

    invites.remove(player);
  }

  public void move(OfflinePlayer player, @Nullable Channel channel) {
    this.leave(player);


    if(channel == null) {
      return;
    }

    channel.join(player);
  }

  public void join(OfflinePlayer player) {
    if(members.contains(player)) {
      return;
    }

    if(moderators.contains(player)) {
      return;
    }

    if(owner.equals(player)) {
      return;
    }

    if(invites.contains(player)) {
      invites.remove(player);
    }

    if(Channel.getChannelO(player) != null) {
      return;
    }

    members.add(player);

    this.message(new MessageBuilder().primary(player.getName()).success(" ist dem Nachrichtenkanal beigetreten."));
  }

  public void leave(OfflinePlayer player) {
    if(owner.equals(player)) {
      this.delete();
      return;
    }

    moderators.remove(player);
    members.remove(player);

    this.message(new MessageBuilder().primary(player.getName()).error(" hat den Nachrichtenkanal verlassen."));
  }

  public boolean delete() {
    this.message(new MessageBuilder().primary("Der Nachrichtenkanal ").info(this.getName()).error(" wurde gelöscht."));

    final UUID uuid = this.getOwner().getUniqueId();

    this.members.clear();
    this.moderators.clear();

    return this.unregister(uuid);
  }

  private void message(MessageBuilder messageBuilder) {
    this.getOnlinePlayers().forEach(player -> SurfChat.message(player, messageBuilder));
  }

  public void register() {
    ChannelProvider.getInstance().getChannels().put(owner.getUniqueId(), this);
  }

  public boolean unregister(UUID uuid) {
    ChannelProvider.getInstance().getChannels().remove(uuid);

    return !ChannelProvider.getInstance().getChannels().containsKey(uuid);
  }

  public static Channel getChannel(String name) {
    return ChannelProvider.getInstance().getChannels().values().stream().filter(channel -> channel.getName().equals(name)).findFirst().orElse(null);
  }

  public static Channel getChannel(CommandSender sender) {
    return ChannelProvider.getInstance().getChannels().values().stream().filter(channel ->  channel.isModerator(sender) || channel.isMember(sender) || channel.isOwner(sender)).findFirst().orElse(null);
  }

  public static Channel getChannelO(OfflinePlayer player) {
    return ChannelProvider.getInstance().getChannels().values().stream().filter(channel ->  channel.getModerators().contains(player) || channel.getMembers().contains(player) || channel.getOwner().equals(player)).findFirst().orElse(null);
  }
}