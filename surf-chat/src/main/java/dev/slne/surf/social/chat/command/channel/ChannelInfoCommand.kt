package dev.slne.surf.social.chat.command.channel

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.social.chat.command.argument.ChannelArgument
import dev.slne.surf.social.chat.`object`.Channel
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit

class ChannelInfoCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withOptionalArguments(ChannelArgument("channel"))
        playerExecutor { player, args ->
            val channel = args.getOrDefaultUnchecked<Channel?>("channel", Channel.getChannel(player)) ?: return@playerExecutor

            player.sendMessage(createInfoMessage(channel))
        }
    }

    private fun createInfoMessage(channel: Channel): Component {
        val owner = channel.owner ?: return buildText { error("Es ist ein Fehler aufgetreten!") }
        return buildText {
            primary("Kanalinformation: ")
            info(channel.name)
            appendNewline()

            darkSpacer("   - ")
            variableKey("Beschreibung: ")
            variableValue(channel.description)
            appendNewline()

            darkSpacer("   - ")
            variableKey("Besitzer: ")
            variableValue(Bukkit.getOfflinePlayer(owner).name ?: "Unbekannt")
            appendNewline()

            darkSpacer("   - ")
            variableKey("Status: ")
            variableValue(if (channel.closed) "Geschlossen" else "Offen")
            appendNewline()

            darkSpacer("   - ")
            variableKey("Mitglieder: ")
            variableValue((channel.members.size + channel.moderators.size + 1).toString())
            appendNewline()

            darkSpacer("   - ")
            variableKey("Einladungen: ")
            variableValue(channel.invites.size.toString())
            appendNewline()
        }
    }
}
