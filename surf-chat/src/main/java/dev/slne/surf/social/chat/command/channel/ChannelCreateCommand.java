package dev.slne.surf.social.chat.command.channel;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.TextArgument;
import dev.slne.surf.social.chat.SurfChat;
import dev.slne.surf.social.chat.object.Channel;
import dev.slne.surf.social.chat.util.MessageBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;

public class ChannelCreateCommand extends CommandAPICommand {

  public ChannelCreateCommand(String commandName) {
    super(commandName);

    withArguments(new TextArgument("name"));
    withArguments(new TextArgument("description"));
    executesPlayer((player, args) -> {
      if(Channel.getChannel(player) != null) {
        SurfChat.message(player, new MessageBuilder().error("Du bist bereits in einem Nachrichtenkanal."));
        return;
      }

      String name = args.getUnchecked("name");
      String description = args.getUnchecked("description");
      Channel channel = Channel.builder()
          .name(name)
          .description(description)
          .members(new ObjectArraySet<>())
          .invites(new ObjectArraySet<>())
          .closed(true)
          .owner(player)
          .build();

      channel.register();

      SurfChat.message(player, new MessageBuilder().primary("Du hast den Nachrichtenkanal ").info(channel.getName()).success(" erstellt."));
    });
  }
}
