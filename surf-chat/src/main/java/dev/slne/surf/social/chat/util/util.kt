package dev.slne.surf.social.chat.util

import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.util.*

fun Iterable<UUID>.mapOfflinePlayerTo(destination: MutableCollection<in OfflinePlayer>) =
    mapNotNullTo(destination) { Bukkit.getOfflinePlayer(it) }
fun Iterable<UUID>.mapOfflinePlayerNamesTo(destination: MutableCollection<in String>) =
    mapNotNullTo(destination) { Bukkit.getOfflinePlayer(it).name }
fun Iterable<UUID>.mapOnlinePlayersTo(destination: MutableCollection<in Player>) =
    mapNotNullTo(destination) { Bukkit.getPlayer(it) }