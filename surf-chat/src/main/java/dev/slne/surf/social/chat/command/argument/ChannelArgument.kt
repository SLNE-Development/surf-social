package dev.slne.surf.social.chat.command.argument

import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.slne.surf.social.chat.`object`.Channel
import dev.slne.surf.social.chat.provider.ChannelProvider
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText

class ChannelArgument(nodeName: String) : CustomArgument<Channel, String>(
    StringArgument(nodeName),
    { info ->
        Channel.getChannel(info.input()) ?: throw CustomArgumentException.fromAdventureComponent(
            buildText {
                error("Unknown channel: ${info.input()}")
            }
        )
    }) {
    init {
        replaceSuggestions(ArgumentSuggestions.stringCollection { ChannelProvider.channels.values.map { it.name } })
    }
}