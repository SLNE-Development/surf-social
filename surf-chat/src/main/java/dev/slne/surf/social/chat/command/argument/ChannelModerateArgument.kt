package dev.slne.surf.social.chat.command.argument

import dev.jorel.commandapi.SuggestionInfo
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.CustomArgument.CustomArgumentInfoParser
import dev.jorel.commandapi.arguments.StringArgument
import dev.slne.surf.social.chat.`object`.Channel
import dev.slne.surf.social.chat.provider.ChannelProvider
import org.bukkit.command.CommandSender

class ChannelModerateArgument(nodeName: String?) :
    CustomArgument<Channel?, String?>(
        StringArgument(nodeName),
        CustomArgumentInfoParser<Channel, String?> { info: CustomArgumentInfo<String?> ->
            val channel: Channel = Channel.Companion.getChannel(info.input())
            if (channel == null || !channel.isModerator(info.sender())) {
                throw CustomArgumentException.fromMessageBuilder(MessageBuilder("Unknown channel: ").appendArgInput())
            } else {
                return@CustomArgument channel
            }
        }) {
    init {
        this.replaceSuggestions(
            ArgumentSuggestions.strings<CommandSender>(
                java.util.function.Function<SuggestionInfo<CommandSender>, Array<String>> { info: SuggestionInfo<CommandSender?> ->
                    ChannelProvider.getInstance().channels.values.stream()
                        .filter { channel: Channel -> channel.isModerator(info.sender()) }
                        .map<String> { obj: Channel -> obj.name }
                        .toArray<String> { _Dummy_.__Array__() }
                })
        )
    }
}