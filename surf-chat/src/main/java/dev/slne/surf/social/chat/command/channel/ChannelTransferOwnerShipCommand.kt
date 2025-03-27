package dev.slne.surf.social.chat.command.channel

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.stringArgument
import dev.slne.surf.social.chat.command.argument.ChannelMembersArgument
import dev.slne.surf.social.chat.`object`.Channel
import dev.slne.surf.social.chat.send
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.clickSuggestsCommand
import net.kyori.adventure.text.Component
import org.bukkit.OfflinePlayer

class ChannelTransferOwnerShipCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withArguments(ChannelMembersArgument("member"))
        stringArgument("confirm")
        playerExecutor { player, args ->
            val channel: Channel? = Channel.getChannel(player)
            val target = args.getUnchecked<OfflinePlayer>("member") ?: return@playerExecutor
            val confirm = args.getOrDefaultUnchecked("confirm", "")

            if (channel == null) {
                player.send {
                    appendPrefix()
                    error("Du bist in keinem Nachrichtenkanal.")
                }
                return@playerExecutor
            }

            if (!channel.isOwner(player)) {
                player.send {
                    appendPrefix()
                    error("Du bist nicht der Besitzer des Nachrichtenkanals.")
                }
                return@playerExecutor
            }

            if (!confirm.equals("confirm", ignoreCase = true) && !confirm.equals("yes", ignoreCase = true) && !confirm.equals("true", ignoreCase = true) && !confirm.equals("ja", ignoreCase = true)) {
                player.send {
                    appendPrefix()
                    error("Bitte bestätige den Vorgang.")
                    append {
                        darkSpacer("[")
                        info("Bestätigen")
                        darkSpacer("]")
                        clickSuggestsCommand("/channel transferOwnership " + target.name + " confirm")
                        hoverEvent(Component.text("Klicke hier, um den Vorgang zu bestätigen.", Colors.INFO))
                    }
                }
                return@playerExecutor
            }

            val owner = channel.owner ?: return@playerExecutor

            channel.unregister(owner)

            channel.moderators.add(channel.owner)
            channel.owner = target.uniqueId
            channel.members.remove(target.uniqueId)

            channel.register()

            player.send {
                appendPrefix()
                primary("Du hast den Besitzer des Nachrichtenkanals an ")
                info(target.name ?: target.uniqueId.toString())
                success(" übergeben.")
            }
            player.send {
                appendPrefix()
                primary("Du wurdest zum Besitzer des Nachrichtenkanals ")
                info(channel.name)
                success(" ernannt.")
            }
        }
    }
}
