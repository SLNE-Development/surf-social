package dev.slne.surf.social.chat

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import dev.jorel.commandapi.CommandAPI
import dev.slne.surf.social.chat.command.*
import dev.slne.surf.social.chat.command.channel.ChannelCommand
import dev.slne.surf.social.chat.listener.PlayerAsyncChatListener
import dev.slne.surf.social.chat.listener.PlayerQuitListener
import dev.slne.surf.social.chat.`object`.Message
import dev.slne.surf.social.chat.permission.SurfChatPermissions
import dev.slne.surf.social.chat.service.ChatFilterService
import dev.slne.surf.social.chat.service.ChatHistoryService
import dev.slne.surf.social.chat.service.DatabaseService
import dev.slne.surf.social.chat.util.MessageBuilder
import dev.slne.surf.surfapi.core.api.messages.Colors
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import java.util.*

class SurfChat : SuspendingJavaPlugin() {
    override suspend fun onEnableAsync() {
        CommandAPI.unregister("msg")
        CommandAPI.unregister("tell")
        CommandAPI.unregister("w")

        PrivateMessageCommand("msg").register()
        ChannelCommand("channel").register()
        SurfChatCommand("surfchat").register()
        IgnoreCommand("ignore").register()
        TogglePmCommand("togglepm").register()
        ReplyCommand("reply").register()

        this.saveDefaultConfig()

        ChatFilterService.loadBlockedWords()
        DatabaseService.connect()

        Bukkit.getPluginManager().registerEvents(PlayerAsyncChatListener(), this)
        Bukkit.getPluginManager().registerEvents(PlayerQuitListener(), this)

        SurfChatPermissions
    }

    override suspend fun onDisableAsync() {
        DatabaseService.saveAll()
        DatabaseService.disconnect()
    }

    companion object {
        val instance: SurfChat get() = getPlugin(SurfChat::class.java)

        fun send(player: OfflinePlayer, text: MessageBuilder, messageID: UUID = UUID.randomUUID()) {
            send(player, text.build(), messageID)
        }

        fun send(player: OfflinePlayer, text: Component, messageID: UUID = UUID.randomUUID()) {
            val message = Colors.PREFIX.append(text)
            val onlinePlayer = player.player ?: return

            onlinePlayer.sendMessage(message)
            ChatHistoryService.insertNewMessage(
                player.uniqueId,
                Message("Unknown", player.name ?: player.uniqueId.toString(), message),
                messageID
            )
        }
    }
}

val plugin get() = SurfChat.instance
