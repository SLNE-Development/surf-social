package dev.slne.surf.social.chat.command.channel;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.OfflinePlayerArgument;
import dev.slne.surf.social.chat.SurfChat;
import dev.slne.surf.social.chat.command.argument.ChannelArgument;
import dev.slne.surf.social.chat.object.Channel;
import dev.slne.surf.social.chat.util.MessageBuilder;
import org.bukkit.OfflinePlayer;

public class ChannelMoveCommand extends CommandAPICommand {

  public ChannelMoveCommand(String commandName) {
    super(commandName);

    withArguments(new OfflinePlayerArgument("player"));
    withOptionalArguments(new ChannelArgument("channel"));
    withPermission("surf.chat.command.channel.move");

    executesPlayer((player, args) -> {
      OfflinePlayer target = args.getUnchecked("player");
      Channel channel = args.getUnchecked("channel");

      channel.move(target, channel);

      SurfChat.message(player, new MessageBuilder().primary("Du hast ").info(target.getName()).primary(" in den Nachrichtenkanal ").info(channel.getName()).success(" verschoben."));
      SurfChat.message(target, new MessageBuilder().primary("Du wurdest in den Nachrichtenkanal ").info(channel.getName()).success(" verschoben."));
    });
  }
}
