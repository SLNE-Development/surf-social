package dev.slne.surf.social.chat.provider

import dev.slne.surf.social.chat.`object`.Channel
import dev.slne.surf.surfapi.core.api.util.mutableObject2ObjectMapOf
import org.bukkit.entity.Player
import java.util.*

object ChannelProvider {
    val channels = mutableObject2ObjectMapOf<UUID, Channel>()

    fun exists(name: String): Boolean = channels.values.any { it.name == name }

    fun handleQuit(player: Player) {
        val channel = Channel.getChannel(player) ?: return

        if (channel.owner == player) {
            channel.delete()
        }
    }
}
