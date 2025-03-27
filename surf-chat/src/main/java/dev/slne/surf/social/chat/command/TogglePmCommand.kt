package dev.slne.surf.social.chat.command

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.social.chat.SurfChat
import dev.slne.surf.social.chat.`object`.ChatUser
import dev.slne.surf.social.chat.send
import dev.slne.surf.social.chat.util.MessageBuilder

class TogglePmCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission("surf.chat.command.toggle")

        playerExecutor{ player, _ ->
            SurfChat.instance.launch {
                val user: ChatUser = ChatUser.getUser(player.uniqueId)

                if (user.toggledPM) {
                    user.toggledPM = false
                    player.send {
                        appendPrefix()
                        success("Du hast privat Nachrichten aktiviert.")
                    }
                } else {
                    user.toggledPM = true
                    player.send {
                        appendPrefix()
                        error("Du hast privat Nachrichten deaktiviert.")
                    }
                }
            }
        }
    }
}
