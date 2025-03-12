package dev.slne.surf.social.chat.command.channel;

import dev.jorel.commandapi.CommandAPICommand;

import dev.slne.surf.social.chat.SurfChat;
import dev.slne.surf.social.chat.command.argument.ChannelInviteArgument;
import dev.slne.surf.social.chat.object.Channel;
import dev.slne.surf.social.chat.util.MessageBuilder;

public class ChannelAcceptInviteCommand extends CommandAPICommand {

  public ChannelAcceptInviteCommand(String commandName) {
    super(commandName);

    withArguments(new ChannelInviteArgument("channel"));
    executesPlayer((player, args) -> {
      Channel channel = args.getUnchecked("channel");

      if(!channel.hasInvite(player)) {
        SurfChat.message(player, new MessageBuilder().primary("Du hast keine Einladung in den Nachrichtenkanal ").info(channel.getName()).error(" erhalten."));
        return;
      }

      channel.acceptInvite(player);

      SurfChat.message(player, new MessageBuilder().primary("Du hast die Einladung in den Nachrichtenkanal ").info(channel.getName()).success(" angenommen."));
    });
  }
}
