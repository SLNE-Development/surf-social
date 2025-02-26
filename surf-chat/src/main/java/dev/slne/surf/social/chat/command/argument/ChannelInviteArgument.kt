package dev.slne.surf.social.chat.command.argument

import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.slne.surf.social.chat.`object`.Channel
import dev.slne.surf.social.chat.provider.ChannelProvider

class ChannelInviteArgument(nodeName: String) :
    CustomArgument<Channel, String>(StringArgument(nodeName), { info ->
            val channel = Channel.getChannel(info.input())
                ?: throw CustomArgumentException.fromMessageBuilder(MessageBuilder("Unknown channel: ").appendArgInput())

            if (!channel.hasInvite(info.sender())) {
                throw CustomArgumentException.fromMessageBuilder(MessageBuilder("Unknown channel: ").appendArgInput())
            }
            channel
        }) {

    init {
        replaceSuggestions(ArgumentSuggestions.stringCollection { info ->
            ChannelProvider.channels.values
                .filter { it.hasInvite(info.sender()) }
                .map { it.name }
            }
        )
    }
}