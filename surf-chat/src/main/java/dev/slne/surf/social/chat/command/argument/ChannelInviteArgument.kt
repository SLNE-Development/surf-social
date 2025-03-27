package dev.slne.surf.social.chat.command.argument

import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.slne.surf.social.chat.`object`.Channel
import dev.slne.surf.social.chat.provider.ChannelProvider
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText

class ChannelInviteArgument(nodeName: String) :
    CustomArgument<Channel, String>(StringArgument(nodeName), { info ->
        val channel = Channel.getChannel(info.input())
            ?: throw CustomArgumentException.fromAdventureComponent(
                buildText {
                    error("Unknown channel: ${info.input()}")
                }
            )

        if (!channel.hasInvite(info.sender())) {
            throw CustomArgumentException.fromAdventureComponent(
                buildText {
                    error("Unknown channel: ${info.input()}")
                }
            )
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