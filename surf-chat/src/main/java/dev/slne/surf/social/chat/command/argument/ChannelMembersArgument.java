package dev.slne.surf.social.chat.command.argument;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;

import dev.slne.surf.social.chat.object.Channel;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import it.unimi.dsi.fastutil.objects.ObjectSet;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class ChannelMembersArgument extends CustomArgument<OfflinePlayer, String> {
  public ChannelMembersArgument(String nodeName) {
    super(new StringArgument(nodeName), info -> {
      OfflinePlayer player = Bukkit.getOfflinePlayer(info.input());
      Channel channel = Channel.getChannelO(player);

      if(channel == null) {
        throw CustomArgumentException.fromMessageBuilder(new MessageBuilder("Du bist in keinem Kanal, oder dieser ist invalid."));
      }

      if(!channel.isMember(player) && !channel.isOwnerO(player) && !channel.isModeratorO(player)) {
        throw CustomArgumentException.fromMessageBuilder(new MessageBuilder("Player is not a member of the channel: ").appendArgInput());
      }

      return player;
    });

    this.replaceSuggestions(ArgumentSuggestions.strings(info -> {
      Channel channel = Channel.getChannel(info.sender());

      if(channel == null) {
        return new String[0];
      }

      final ObjectSet<String> members = new ObjectArraySet<>();

      members.addAll(channel.getMembers().stream().map(OfflinePlayer::getName).toList());
      members.addAll(channel.getModerators().stream().map(OfflinePlayer::getName).toList());
      members.add(channel.getOwner().getName());
      members.remove(info.sender().getName());

      return members.toArray(String[]::new);
    }));
  }
}