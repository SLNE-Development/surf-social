package dev.slne.surf.social.chat.command

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.offlinePlayerArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.social.chat.`object`.ChatUser
import dev.slne.surf.social.chat.plugin
import dev.slne.surf.social.chat.send
import dev.slne.surf.social.chat.util.Components
import org.bukkit.OfflinePlayer

class IgnoreCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission("surf.chat.command.ignore")
        offlinePlayerArgument("target")

        playerExecutor { player, args ->
            val target: OfflinePlayer by args
            val targetUuid = target.uniqueId

            plugin.launch {
                val user = ChatUser.getUser(player.uniqueId)

                if (targetUuid == player.uniqueId) {
                    player.send {
                        appendPrefix()
                        Components.cannotIgnoreSelf
                    }
                    return@launch
                }

                if (user.isIgnoring(targetUuid)) {
                    user.ignoreList.remove(targetUuid)
                    player.send {
                        appendPrefix()
                        Components.getIgnoreComponent(target, false)
                    }
                } else {
                    user.ignoreList.add(targetUuid)
                    player.send {
                        appendPrefix()
                        Components.getIgnoreComponent(target, true)
                    }
                }
            }
        }
    }
}
