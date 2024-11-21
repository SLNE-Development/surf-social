package dev.slne.surf.friends.command.subcommand

import com.github.shynixn.mccoroutine.bukkit.launch
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.OfflinePlayerArgument
import dev.jorel.commandapi.arguments.SafeSuggestions
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor

import dev.slne.surf.friends.FriendManager
import dev.slne.surf.friends.SurfFriendsPlugin


import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

class FriendAcceptCommand(name: String) : CommandAPICommand(name) {
    init {
        withArguments(OfflinePlayerArgument("target").replaceSafeSuggestions(
            SafeSuggestions.suggest {
                Bukkit.getOnlinePlayers().toTypedArray<Player>()
            })
        )

        executesPlayer(PlayerCommandExecutor { player: Player, args: CommandArguments ->
            val target = args.getUnchecked<OfflinePlayer>("target") ?: throw CommandAPI.failWithString("Der Spieler wurde nicht gefunden.")

            SurfFriendsPlugin.instance.launch {
                if (!FriendManager.hasFriendRequest(player.uniqueId, target.uniqueId)) {
                    throw CommandAPI.failWithString("Du hast keine Freundschaftsanfrage von " + target.name)
                }
                FriendManager.acceptFriendRequest(player.uniqueId, target.uniqueId)
            }
        })
    }
}
