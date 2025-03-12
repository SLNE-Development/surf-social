package dev.slne.surf.social.chat.command.channel;

import dev.jorel.commandapi.CommandAPICommand;
import dev.slne.surf.social.chat.SurfChat;
import dev.slne.surf.social.chat.command.argument.ChannelArgument;
import dev.slne.surf.social.chat.object.Channel;
import dev.slne.surf.social.chat.util.MessageBuilder;

public class ChannelForceJoinCommand extends CommandAPICommand {

  public ChannelForceJoinCommand(String commandName) {
    super(commandName);

    withPermission("surf.chat.command.channel.forcejoin");
    withArguments(new ChannelArgument("channel"));
    executesPlayer((player, args) -> {
      Channel channel = args.getUnchecked("channel");

      if(Channel.getChannelO(player) != null) {
        SurfChat.message(player, new MessageBuilder().error("Du bist bereits in einem Nachrichtenkanal."));
        return;
      }

      channel.join(player);

      SurfChat.message(player, new MessageBuilder().primary("Du bist dem Nachrichtenkanal ").info(channel.getName()).success(" beigetreten."));
    });
  }
}
