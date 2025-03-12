package dev.slne.surf.social.chat.command.channel;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.slne.surf.social.chat.object.Channel;
import dev.slne.surf.social.chat.provider.ChannelProvider;
import dev.slne.surf.social.chat.util.MessageBuilder;
import dev.slne.surf.social.chat.util.PageableMessageBuilder;
import net.kyori.adventure.text.Component;

public class ChannelListCommand extends CommandAPICommand {
  public ChannelListCommand(String commandName) {
    super(commandName);

    withOptionalArguments(new IntegerArgument("page"));
    executesPlayer((player, args ) -> {
      PageableMessageBuilder message = new PageableMessageBuilder();
      Integer page = args.getOrDefaultUnchecked("page", 1);

      int index = 0;

      message.setPageCommand("/channel list %page%");

      for (Channel channel : ChannelProvider.getInstance().getChannels().values()) {
        index ++;

        message.addLine(new MessageBuilder().variableValue(index + ". ").primary(channel.getName()).darkSpacer(" (").info(String.valueOf(channel.getMembers().size() + channel.getModerators().size() + 1)).darkSpacer(")").build().hoverEvent(this.createInfoMessage(channel)));
      }

      message.send(player, page);
    });
  }

  private Component createInfoMessage(Channel channel) {
    return new MessageBuilder()
        .primary("Kanalinformation: ").info(channel.getName()).newLine()
        .darkSpacer("   - ").variableKey("Beschreibung: ").variableValue(channel.getDescription()).newLine()
        .darkSpacer("   - ").variableKey("Besitzer: ").variableValue(channel.getOwner().getName()).newLine()
        .darkSpacer("   - ").variableKey("Status: ").variableValue(channel.isClosed() ? "Geschlossen" : "Offen").newLine()
        .darkSpacer("   - ").variableKey("Mitglieder: ").variableValue(String.valueOf(channel.getMembers().size() + channel.getModerators().size() + 1)).newLine()
        .darkSpacer("   - ").variableKey("Einladungen: ").variableValue(String.valueOf(channel.getInvites().size())).newLine()
        .build();
  }
}
