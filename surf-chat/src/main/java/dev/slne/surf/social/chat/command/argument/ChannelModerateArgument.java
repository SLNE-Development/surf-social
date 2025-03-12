package dev.slne.surf.social.chat.command.argument;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.slne.surf.social.chat.object.Channel;
import dev.slne.surf.social.chat.provider.ChannelProvider;

public class ChannelModerateArgument extends CustomArgument<Channel, String> {
  public ChannelModerateArgument(String nodeName) {
    super(new StringArgument(nodeName), info -> {
      Channel channel = Channel.getChannel(info.input());

      if (channel == null || !channel.isModerator(info.sender())) {
        throw CustomArgumentException.fromMessageBuilder(new MessageBuilder("Unknown channel: ").appendArgInput());
      } else {
        return channel;
      }
    });

    this.replaceSuggestions(ArgumentSuggestions.strings(info -> ChannelProvider.getInstance().getChannels().values().stream().filter(channel -> channel.isModerator(info.sender())).map(Channel::getName).toArray(String[]::new)));
  }
}