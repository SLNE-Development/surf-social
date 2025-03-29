package dev.slne.surf.social.chat.command.blacklist

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.social.chat.SurfChat
import dev.slne.surf.social.chat.command.WordBlacklistCommand
import dev.slne.surf.social.chat.`object`.ChatPunishment
import dev.slne.surf.social.chat.provider.WordBlacklistProvider
import dev.slne.surf.social.chat.util.MessageBuilder
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class BlacklistAddCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission("surf.chat.command.surf-chat")
        withAliases("modify")
        textArgument("word", false)
        argument(WordBlacklistCommand.punishment)
        argument(WordBlacklistCommand.duration)
        argument(WordBlacklistCommand.message)
        anyExecutor { sender, arguments ->
            val word = arguments.get("word") as String
            val punishment = getPunishment(arguments.getUnchecked<String>("punishment"))
            val duration = parseDuration(arguments.getUnchecked<String>("duration"))
            val msg = arguments.getUnchecked<String>("message")
            val cp = ChatPunishment(punishment, duration?.toInt(DurationUnit.SECONDS), msg)
            if (WordBlacklistProvider.getPunishment(word) != null) SurfChat.send(MessageBuilder().info(cp.getUpdateMessage(word)).build(), sender)
            else SurfChat.send(MessageBuilder().info(cp.getCreationMessage(word)).build(), sender)
            WordBlacklistProvider.addPunishment(word, cp)
        }
    }

    companion object{
        fun parseDuration(value:String?):Duration? {
            if (value == null || value == "null") return Duration.ZERO
            var time = Duration.ZERO
            var temp = ""
            for (char in value.toCharArray()){
                if(Regex("[0-9]").matches(char.toString())){
                    temp+=char
                }else{
                    when (char){
                    'd' -> time = time.plus(temp.toInt().toDuration(DurationUnit.DAYS))
                    'h' -> time = time.plus(temp.toInt().toDuration(DurationUnit.HOURS))
                    'm' -> time = time.plus(temp.toInt().toDuration(DurationUnit.MINUTES))
                    's' -> time = time.plus(temp.toInt().toDuration(DurationUnit.SECONDS))
                    }
                    temp = ""
                }
            }
            if (temp != "") time.plus(temp.toInt().toDuration(DurationUnit.SECONDS))
            if (time.toInt(DurationUnit.SECONDS) > 0){
                return time
            }
            return null
        }
        fun getPunishment(value:String?):ChatPunishment.Punishment{
            return if (value == null) ChatPunishment.Punishment.BLOCK
            else ChatPunishment.Punishment.valueOf(value)
        }
    }
}