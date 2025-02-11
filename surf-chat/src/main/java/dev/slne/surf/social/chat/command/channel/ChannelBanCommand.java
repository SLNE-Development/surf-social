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
        return;
      }

      if(!channel.isModerator(player)) {
        return;
      }

      channel.ban(target);

      SurfChat.message(player, new MessageBuilder().primary("Du hast ").secondary(target.getName()).primary(" aus dem Nachrichtenkanal ").secondary(channel.getName()).info(" verbannt."));
      SurfChat.message(target, new MessageBuilder().primary("Du wurdest aus dem Nachrichtenkanal ").secondary(channel.getName()).info(" verbannt."));
    });
  }
}
