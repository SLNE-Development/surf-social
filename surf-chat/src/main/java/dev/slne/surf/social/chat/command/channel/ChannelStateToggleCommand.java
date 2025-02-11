package dev.slne.surf.social.chat.command.channel;

import dev.jorel.commandapi.CommandAPICommand;
import dev.slne.surf.social.chat.SurfChat;
import dev.slne.surf.social.chat.object.Channel;
import dev.slne.surf.social.chat.util.MessageBuilder;

public class ChannelStateToggleCommand extends CommandAPICommand {

  public ChannelStateToggleCommand(String commandName) {
    super(commandName);

    executesPlayer((player, args) -> {
      Channel channel = Channel.getChannel(player);

      if(channel == null) {
        return;
      }

      if(!channel.isOwner(player)) {
        return;
      }

     if(channel.isClosed()) {
       channel.setClosed(false);
       SurfChat.message(player, new MessageBuilder().primary("Der Nachrichtenkanal ").info(channel.getName()).primary(" ist nun ").success("öffentlich."));
     } else {
       channel.setClosed(true);
       SurfChat.message(player, new MessageBuilder().primary("Der Nachrichtenkanal ").info(channel.getName()).primary(" ist nun ").error("privat."));
     }
    });
  }
}
