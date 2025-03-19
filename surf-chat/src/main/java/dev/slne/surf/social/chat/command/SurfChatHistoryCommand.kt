package dev.slne.surf.social.chat.command

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.OfflinePlayerArgument
import dev.jorel.commandapi.arguments.PlayerArgument
import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.social.chat.SurfChat
import dev.slne.surf.social.chat.`object`.ChatUser
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class SurfChatHistoryCommand(commandName:String):CommandAPICommand(commandName) {
    init {
        withPermission("surf.chat.command.history")

        offlinePlayerArgument("player", true)

        playerExecutor{player, args ->
            var target: OfflinePlayer? = args.getUnchecked<OfflinePlayer>("player")
            if(target == null) target = player
            player.sendMessage("Loading User \"${target.name}\"...")
            //Theoretisch bräuchte ich hier eine Möglichkeit die Existenz zu prüfen
            SurfChat.instance.launch {
                val chatPlayer = ChatUser.getUser(target.uniqueId)

                val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm z")

                for (msg in chatPlayer.getSentMessages()){
                    val time = ZonedDateTime.ofInstant(msg.date.toInstant(), ZoneId.systemDefault())
                    player.sendMessage(Component.text("["+time.format(formatter)+"] ", TextColor.color(100, 100, 100)).append(
                        Component.text(msg.type.getMessage() + " >> ", msg.type.getColor())).append(Component.text(msg.message, TextColor.color(255, 255, 255))))

                }
            }

        }
        consoleExecutor { consoleCommandSender, args ->
            val target: OfflinePlayer? = args.getUnchecked<OfflinePlayer>("player")
            if(target != null)consoleCommandSender.sendMessage("Loading User \"${target.name}\"...")
            //Theoretisch bräuchte ich hier eine Möglichkeit die Existenz zu prüfen
            if (target == null) {
                consoleCommandSender.sendMessage("Invalid Player")
                return@consoleExecutor
            }
            SurfChat.instance.launch {
                val chatPlayer = ChatUser.getUser(target.uniqueId)
                val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm z")
                for (msg in chatPlayer.getSentMessages()){
                    val time = ZonedDateTime.ofInstant(msg.date.toInstant(), ZoneId.systemDefault())
                    consoleCommandSender.sendMessage(Component.text("["+time.format(formatter)+"] ", TextColor.color(100, 100, 100)).append(
                        Component.text(msg.type.getMessage() + " >> ", msg.type.getColor())).append(Component.text(msg.message, TextColor.color(255, 255, 255))))

                }
            }
        }
    }
}