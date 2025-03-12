package dev.slne.surf.social.chat.command.channel;

import dev.jorel.commandapi.CommandAPICommand;
import dev.slne.surf.social.chat.SurfChat;
import dev.slne.surf.social.chat.command.argument.ChannelArgument;
import dev.slne.surf.social.chat.object.Channel;
import dev.slne.surf.social.chat.util.MessageBuilder;

public class ChannelForceDeleteCommand extends CommandAPICommand {

  public ChannelForceDeleteCommand(String commandName) {
    super(commandName);

    withPermission("surf.chat.command.channel.forceDelete");
    withArguments(new ChannelArgument("channel"));
    executesPlayer((player, args) -> {
      Channel channel = args.getUnchecked("channel");

      if(!channel.delete()) {
        SurfChat.message(player, new MessageBuilder().error("Der Nachrichtenkanal konnte nicht gelöscht werden."));
        return;
      }

      SurfChat.message(player, new MessageBuilder().primary("Der Nachrichtenkanal ").info(channel.getName()).error(" wurde gelöscht."));
    });
  }
}
