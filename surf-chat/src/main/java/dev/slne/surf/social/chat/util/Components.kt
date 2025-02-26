package dev.slne.surf.social.chat.util

import dev.slne.surf.social.chat.`object`.Channel
import dev.slne.surf.social.chat.permission.SurfChatPermissions
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.appendText
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.text
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.util.*

object Components {

    val cannotIgnoreSelf = text("Du kannst dich nicht selbst stummschalten.", Colors.ERROR)

    fun getDeleteComponent(player: Player?, id: UUID): Component {
        if (player == null || !player.hasPermission(SurfChatPermissions.deletePerms)) {
            return Component.empty()
        }

        return buildText {
            appendText("[", Colors.DARK_SPACER)
            appendText("DEL", Colors.VARIABLE_KEY)
            appendText("] ", Colors.DARK_SPACER)

            clickEvent(ClickEvent.runCommand("/surfchat delete $id"))
            hoverEvent(text("Nachricht löschen", Colors.ERROR))
        }
    }

    fun getChannelComponent(channel: Channel) = buildText {
        appendText("[", Colors.DARK_SPACER)
        appendText(channel.name, Colors.VARIABLE_KEY)
        appendText("] ", Colors.DARK_SPACER)
    }

    fun getTeleportComponent(player: Player?, name: String): Component {
        if (player == null || !player.hasPermission(SurfChatPermissions.teleportPerms)) {
            return Component.empty()
        }

        return buildText {
            appendText("[", Colors.DARK_SPACER)
            appendText("TP", Colors.VARIABLE_KEY)
            appendText("] ", Colors.DARK_SPACER)

            clickEvent(ClickEvent.runCommand("/teleport $name"))
            hoverEvent(text("Zum Spieler teleportieren", Colors.INFO))
        }
    }

    fun getIgnoreComponent(target: OfflinePlayer, ignored: Boolean) = buildText {
        appendText("Du hast ", Colors.SUCCESS)
        appendText(target.name ?: target.uniqueId.toString(), Colors.VARIABLE_VALUE)
        appendText(if (!ignored) " entstummt." else " stumm geschaltet.", Colors.SUCCESS)
    }
}