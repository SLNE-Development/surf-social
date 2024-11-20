package dev.slne.surf.friends.listener

import com.github.shynixn.mccoroutine.bukkit.launch
import dev.slne.surf.friends.FriendManager
import dev.slne.surf.friends.SurfFriendsPlugin
import kotlinx.coroutines.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

@DelicateCoroutinesApi
class PlayerQuitListener : Listener {

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        val player = event.player

        SurfFriendsPlugin.instance.launch {
            FriendManager.saveFriendData(player.uniqueId)
        }
    }
}
