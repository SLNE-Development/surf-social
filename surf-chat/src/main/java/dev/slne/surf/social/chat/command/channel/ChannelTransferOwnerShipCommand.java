package dev.slne.surf.social.chat.command.channel;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.slne.surf.social.chat.SurfChat;
import dev.slne.surf.social.chat.command.argument.ChannelMembersArgument;
import dev.slne.surf.social.chat.object.Channel;
import dev.slne.surf.social.chat.util.MessageBuilder;
import org.bukkit.OfflinePlayer;

public class ChannelTransferOwnerShipCommand extends CommandAPICommand {
  public ChannelTransferOwnerShipCommand(String commandName) {
    super(commandName);

    withArguments(new ChannelMembersArgument("member"));
    withOptionalArguments(new StringArgument("confirm"));
    executesPlayer((player, args) -> {
      Channel channel = Channel.getChannel(player);
      OfflinePlayer target = args.getUnchecked("member");
      String confirm = args.getOrDefaultUnchecked("confirm", "");

      if(channel == null) {
        SurfChat.message(player, new MessageBuilder().error("Du bist in keinem Nachrichtenkanal."));
        return;
      }

      if(!channel.isOwner(player)) {
        SurfChat.message(player, new MessageBuilder().error("Du bist nicht der Besitzer des Nachrichtenkanals."));
        return;
      }

      if(!confirm.equalsIgnoreCase("confirm") && !confirm.equalsIgnoreCase("yes") && !confirm.equalsIgnoreCase("true") && !confirm.equalsIgnoreCase("ja")) {
        SurfChat.message(player, new MessageBuilder().error("Bitte bestätige den Vorgang.").command(
            new MessageBuilder().darkSpacer(" [").info("Bestätigen").darkSpacer("]"),
            new MessageBuilder().info("Klicke hier, um den Vorgang zu bestätigen."),
            "/channel transferOwnership " + target.getName() + " confirm"
        ));
        return;
      }

      channel.unregister(channel.getOwner().getUniqueId());

      channel.getModerators().add(channel.getOwner());
      channel.setOwner(target);
      channel.getMembers().remove(target);

      channel.register();

      SurfChat.message(player, new MessageBuilder().primary("Du hast den Besitzer des Nachrichtenkanals an ").info(target.getName()).success(" übergeben."));
      SurfChat.message(target, new MessageBuilder().primary("Du wurdest zum Besitzer des Nachrichtenkanals ").info(channel.getName()).success(" ernannt."));
    });
  }
}
