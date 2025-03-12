package dev.slne.surf.social.chat.command.channel;

import dev.jorel.commandapi.CommandAPICommand;
import dev.slne.surf.social.chat.SurfChat;
import dev.slne.surf.social.chat.command.argument.ChannelMembersArgument;
import dev.slne.surf.social.chat.object.Channel;
import dev.slne.surf.social.chat.util.MessageBuilder;
import org.bukkit.OfflinePlayer;

public class ChannelPromoteCommand extends CommandAPICommand {

  public ChannelPromoteCommand(String commandName) {
    super(commandName);

    withArguments(new ChannelMembersArgument("player"));
    executesPlayer((player, args) -> {
      Channel channel = Channel.getChannel(player);
      OfflinePlayer target = args.getUnchecked("player");

      if(channel == null) {
        SurfChat.message(player, new MessageBuilder().error("Du bist in keinem Nachrichtenkanal."));
        return;
      }

      if(!channel.isOwner(player)) {
        SurfChat.message(player, new MessageBuilder().error("Du bist nicht der Besitzer des Nachrichtenkanals."));
        return;
      }

      channel.promote(target);

      SurfChat.message(player, new MessageBuilder().primary("Du hast den Spieler ").info(target.getName()).success(" befördert."));
      SurfChat.message(target, new MessageBuilder().primary("Du wurdest ").success("befördert"));
    });
  }
}
