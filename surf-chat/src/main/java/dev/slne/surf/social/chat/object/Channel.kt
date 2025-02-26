package dev.slne.surf.social.chat.`object`

import dev.slne.surf.social.chat.SurfChat
import dev.slne.surf.social.chat.provider.ChannelProvider
import dev.slne.surf.social.chat.util.MessageBuilder
import dev.slne.surf.social.chat.util.mapOnlinePlayersTo
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.appendText
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import it.unimi.dsi.fastutil.objects.ObjectSet
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

class Channel(
    var owner: UUID? = null,
    val members: ObjectSet<UUID> = mutableObjectSetOf(),
    val moderators: ObjectSet<UUID> = mutableObjectSetOf(),
    val bannedPlayers: ObjectSet<UUID> = mutableObjectSetOf(),
    val invites: ObjectSet<UUID> = mutableObjectSetOf(),
    val name: String = UUID.randomUUID().toString(),
    val description: String = "Ein cooler Kanal!",
    var closed: Boolean = true
) {

    fun isMember(uuid: UUID): Boolean {
        return members.contains(uuid)
    }

    fun isMember(player: CommandSender): Boolean {
        if (player is OfflinePlayer) {
            return members.contains(player.uniqueId)
        }

        return false
    }

    fun isOwner(uuid: UUID): Boolean = owner == uuid
    fun isOwner(player: CommandSender): Boolean {
        if (player is OfflinePlayer) {
            return owner == player.uniqueId
        }

        return false
    }

    fun hasInvite(uuid: UUID): Boolean = invites.contains(uuid)
    fun hasInvite(player: CommandSender): Boolean {
        if (player is OfflinePlayer) {
            return invites.contains(player.uniqueId)
        }

        return false
    }

    fun isModerator(uuid: UUID): Boolean = moderators.contains(uuid)
    fun isModerator(player: CommandSender): Boolean {
        if (player is OfflinePlayer) {
            return moderators.contains(player.uniqueId)
        }

        return false
    }

    fun invite(uuid: UUID) {
        if (isMember(uuid) || isModerator(uuid) || isOwner(uuid) || bannedPlayers.contains(uuid)) return
        invites.add(uuid)
    }

    fun ban(uuid: UUID) {
        if (isMember(uuid) || isOwner(uuid)) return

        if (bannedPlayers.add(uuid)) {
            this.leave(uuid)
        }
    }

    fun unban(uuid: UUID) {
        bannedPlayers.remove(uuid)
    }

//    val onlinePlayers: ObjectSet<Player>
//        get() {
//            val players: ObjectSet<Player> = ObjectArraySet()
//
//            players.addAll(
//                members.stream().filter { obj: OfflinePlayer -> obj.isOnline }
//                    .map { obj: OfflinePlayer -> obj.player }
//                    .toList()
//            )
//            players.addAll(
//                moderators.stream().filter { obj: OfflinePlayer -> obj.isOnline }
//                    .map { obj: OfflinePlayer -> obj.player }
//                    .toList()
//            )
//
//            val owner = this.owner ?: return players
//
//            if (owner.isOnline) {
//                players.add(owner.player)
//            }
//
//            return players
//        }

    val onlinePlayers: ObjectSet<Player>
        get() {
            val players = mutableObjectSetOf<Player>()
            members.mapOnlinePlayersTo(players)
            moderators.mapOnlinePlayersTo(players)
            owner?.let { Bukkit.getPlayer(it)?.let(players::add) }
            return players
        }

    fun promote(uuid: UUID) {
        if (!isMember(uuid) || isOwner(uuid) || isModerator(uuid)) return

        members.remove(uuid)
        moderators.add(uuid)
    }

    fun demote(uuid: UUID) {
        moderators.remove(uuid)
    }

    fun kick(uuid: UUID) {
        if (!isMember(uuid) || isOwner(uuid)) return
        this.leave(uuid)
    }

    fun acceptInvite(player: CommandSender) {
        if (player is OfflinePlayer) {
            acceptInvite(player.uniqueId)
        }
    }

    fun acceptInvite(uuid: UUID) {
        if (!hasInvite(uuid)) return

        getChannel(uuid)?.leave(uuid)
        join(uuid)
    }

    fun revokeInvite(uuid: UUID) {
        invites.remove(uuid)
    }

    fun move(uuid: UUID, channel: Channel?) {
        leave(uuid)
        channel?.join(uuid)
    }

    fun join(uuid: UUID) {
        if (isModerator(uuid) || isOwner(uuid)) return

        if (invites.remove(uuid)) {
            if (getChannel(uuid) != null) {
                return
            }

            if (members.add(uuid)) {
                message(
                    buildText {
                        appendText(
                            Bukkit.getOfflinePlayer(uuid).name ?: uuid.toString(),
                            Colors.VARIABLE_VALUE
                        )
                        appendText(" ist dem Nachrichtenkanal beigetreten.", Colors.SUCCESS)
                    }
                )
            }
        }
    }

    fun leave(uuid: UUID) {
        if (isOwner(uuid)) {
            delete()
            return
        }

        val modRemoved = moderators.remove(uuid)
        val memberRemoved = members.remove(uuid)

        if (modRemoved || memberRemoved) {
            message(
                buildText {
                    appendText(
                        Bukkit.getOfflinePlayer(uuid).name ?: uuid.toString(),
                        Colors.VARIABLE_VALUE
                    )
                    appendText(" hat den Nachrichtenkanal verlassen.", Colors.ERROR)
                }
            )
        }
    }

    fun delete(): Boolean {
        message(buildText {
            appendText("Der Nachrichtenkanal ", Colors.ERROR)
            appendText(name, Colors.VARIABLE_VALUE)
            appendText(" wurde gelöscht.", Colors.ERROR)
        })

        val owner = this.owner ?: return false

        members.clear()
        moderators.clear()

        return this.unregister(owner)
    }

    private fun message(messageBuilder: MessageBuilder) {
        onlinePlayers.forEach { player -> SurfChat.send(player, messageBuilder) }
    }

    private fun message(message: Component) {
        onlinePlayers.forEach { SurfChat.send(it, message) }
    }

    fun register() {
        ChannelProvider.channels[owner ?: return] = this
    }

    fun unregister(uuid: UUID): Boolean {
        return ChannelProvider.channels.remove(uuid) != null
    }

    companion object {
        fun getChannel(name: String) = ChannelProvider.channels.values.find { it.name == name }
        fun getChannel(sender: CommandSender) = ChannelProvider.channels.values.find {
            it.isModerator(sender) || it.isMember(sender) || it.isOwner(sender)
        }

        fun getChannel(playerUuid: UUID) = ChannelProvider.channels.values.find {
            it.isMember(playerUuid) || it.isModerator(playerUuid) || it.isOwner(playerUuid)
        }
    }
}