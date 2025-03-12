package dev.slne.surf.social.chat.command.channel;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;

import dev.slne.surf.social.chat.SurfChat;
import dev.slne.surf.social.chat.object.Channel;
import dev.slne.surf.social.chat.util.MessageBuilder;
import dev.slne.surf.social.chat.util.PageableMessageBuilder;

import org.bukkit.OfflinePlayer;

public class ChannelMembersCommand extends CommandAPICommand {

  public ChannelMembersCommand(String commandName) {
    super(commandName);

    withOptionalArguments(new IntegerArgument("page"));
    executesPlayer((player, args ) -> {
      PageableMessageBuilder message = new PageableMessageBuilder();
      Integer page = args.getOrDefaultUnchecked("page", 1);
      Channel channel = Channel.getChannelO(player);

      if(channel == null) {
        SurfChat.message(player, new MessageBuilder().error("Du bist in keinem Nachrichtenkanal."));
        return;
      }

      int index = 1;

      message.setPageCommand("/channel members " + channel.getName() + " %page%");

      message.addLine(new MessageBuilder().variableValue(index + ". ").primary(channel.getOwner().getName()).darkSpacer(" (Besitzer)").build());

      for (OfflinePlayer moderator : channel.getModerators()) {
        index ++;

        message.addLine(new MessageBuilder().variableValue(index + ". ").primary(moderator.getName()).darkSpacer(" (Moderator)").build());
      }

      for (OfflinePlayer member : channel.getMembers()) {
        index ++;

        message.addLine(new MessageBuilder().variableValue(index + ". ").primary(member.getName()).darkSpacer(" (Mitglied)").build());
      }

      message.send(player, page);
    });
  }
}
