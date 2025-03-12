package dev.slne.surf.social.chat.command.argument;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.slne.surf.social.chat.object.Channel;
import dev.slne.surf.social.chat.provider.ChannelProvider;

public class ChannelInviteArgument extends CustomArgument<Channel, String> {
  public ChannelInviteArgument(String nodeName) {
    super(new StringArgument(nodeName), info -> {
      Channel channel = Channel.getChannel(info.input());

      if (channel == null || !channel.hasInvite(info.sender())) {
        throw CustomArgumentException.fromMessageBuilder(new MessageBuilder("Unknown channel: ").appendArgInput());
      } else {
        return channel;
      }
    });

    this.replaceSuggestions(ArgumentSuggestions.strings(info -> ChannelProvider.getInstance().getChannels().values().stream().filter(channel -> channel.hasInvite(info.sender())).map(Channel::getName).toArray(String[]::new)));
  }
}