package dev.slne.surf.social.chat.command.blacklist

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.argument
import dev.slne.surf.social.chat.SurfChat
import dev.slne.surf.social.chat.command.WordBlacklistCommand
import dev.slne.surf.social.chat.`object`.ChatPunishment
import dev.slne.surf.social.chat.provider.ConfigurationProvider
import dev.slne.surf.social.chat.util.MessageBuilder
import kotlin.time.Duration
import kotlin.time.DurationUnit

class BlacklistDefaultCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        argument(WordBlacklistCommand.punishment)
        argument(WordBlacklistCommand.duration)
        argument(WordBlacklistCommand.message)
        anyExecutor { sender, args ->
            val punishment = args.getUnchecked<String>("punishment") ?: return@anyExecutor
            var cp = ConfigurationProvider.getDefaultPunishment(ChatPunishment.Punishment.valueOf(punishment))
            val duration = BlacklistAddCommand.parseDuration(args.getUnchecked<String>("duration"))
            val message = args.getUnchecked<String>("message") ?:cp.getRawMessage()
            if (duration == Duration.ZERO && message == null){
                SurfChat.send(MessageBuilder().info(cp.getInfoMessage("default")).build(), sender)
            }else{
                cp = ChatPunishment(cp.getPunishment(), duration?.toInt(DurationUnit.SECONDS), message)
                ConfigurationProvider.updateDefaultPunishment(cp)
                SurfChat.send(MessageBuilder().info(cp.getUpdateMessage("default")).build(), sender)
            }
        }
    }
}