package dev.slne.surf.social.chat.command.channel;

import dev.jorel.commandapi.CommandAPICommand;
import dev.slne.surf.social.chat.SurfChat;
import dev.slne.surf.social.chat.command.argument.ChannelMembersArgument;
import dev.slne.surf.social.chat.object.Channel;
import dev.slne.surf.social.chat.util.MessageBuilder;
import org.bukkit.OfflinePlayer;

public class ChannelBanCommand extends CommandAPICommand {

  public ChannelBanCommand(String commandName) {
    super(commandName);

    withArguments(new ChannelMembersArgument("player"));
    executesPlayer((player, args) -> {
      Channel channel = Channel.getChannel(player);
      OfflinePlayer target = args.getUnchecked("player");

      if(channel == null) {
        SurfChat.message(player, new MessageBuilder().error("Du bist in keinem Nachrichtenkanal."));
        return;
      }

      if(!channel.isModerator(player) && !channel.isOwner(player)) {
        SurfChat.message(player, new MessageBuilder().primary("Du bist ").error("kein Moderator ").primary("in deinem Kanal."));
        return;
      }

      channel.ban(target);

      SurfChat.message(player, new MessageBuilder().primary("Du hast ").info(target.getName()).primary(" aus dem Nachrichtenkanal ").info(channel.getName()).error(" verbannt."));
      SurfChat.message(target, new MessageBuilder().primary("Du wurdest aus dem Nachrichtenkanal ").info(channel.getName()).error(" verbannt."));
    });
  }
}
