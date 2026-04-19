package dev.slne.surf.social.velocity.command

import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.literalArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.api.core.command.args.awaiting
import dev.slne.surf.api.core.messages.adventure.clickOpensUrl
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.api.velocity.command.executors.anyExecutorSuspend
import dev.slne.surf.core.api.common.player.SurfPlayer
import dev.slne.surf.core.api.velocity.command.argument.surfOfflinePlayerArgument
import dev.slne.surf.social.api.SurfSocialApi
import dev.slne.surf.social.api.connection.impl.DiscordConnection
import dev.slne.surf.social.api.connection.impl.TwitchConnection
import dev.slne.surf.social.api.findConnection
import dev.slne.surf.social.velocity.config
import dev.slne.surf.social.velocity.permission.SocialPermissions

fun linkCommand() = commandTree("link") {
    withPermission(SocialPermissions.COMMAND_LINK)

    literalArgument("twitch") {
        playerExecutor { player, _ ->
            player.sendText {
                val url = "https://id.twitch.tv/oauth2/authorize" +
                        "?client_id=${config.twitchClientId}" +
                        "&redirect_uri=https://stats.castcrafter.de/api/auth/twitch/callback" +
                        "&response_type=code" +
                        "&state=MINECARFT_UUID"

                appendInfoPrefix()
                info("Bitte verifiziere deinen Twitch Account hier: ")
                append {
                    spacer("[")
                    variableValue("Verifizierungslink")
                    spacer("]")
                    clickOpensUrl(url)
                }
            }
        }
    }

    literalArgument("discord") {
        playerExecutor { player, _ ->
            player.sendText {
                appendWarningPrefix()
                warning("Du kannst dich derzeit nicht manuell mit deinem Discord Account verbinden.")
            }
        }
    }

    literalArgument("debug") {
        withPermission(SocialPermissions.COMMAND_LINK_DEBUG)
        surfOfflinePlayerArgument("target") {
            anyExecutorSuspend { sender, arguments ->
                val target = arguments.awaiting<SurfPlayer?>("target")

                if (target == null) {
                    sender.sendText {
                        appendErrorPrefix()
                        error("Der Spieler wurde nicht gefunden.")
                    }
                    return@anyExecutorSuspend
                }

                val start = System.currentTimeMillis()

                val discordConnection = SurfSocialApi.findConnection<DiscordConnection>(target.uuid)
                val twitchConnection = SurfSocialApi.findConnection<TwitchConnection>(target.uuid)

                sender.sendText {
                    appendInfoPrefix()
                    info("Der Spieler ")

                    if (discordConnection == null && twitchConnection == null) {
                        error("hat keine Verbindungen.")
                    }

                    if (discordConnection != null && twitchConnection != null) {
                        info("ist mit dem ")
                        variableValue("Twitch Account '${twitchConnection.twitchName}'")
                        info(" und dem ")
                        variableValue("Discord Account '${discordConnection.discordName}'")
                        info(" verbunden.")
                    }

                    if (discordConnection != null && twitchConnection == null) {
                        info("ist mit dem ")
                        variableValue("Discord Account '${discordConnection.discordName}'")
                        info(" verbunden.")
                    }

                    if (twitchConnection != null && discordConnection == null) {
                        info("ist mit dem ")
                        variableValue("Twitch Account '${twitchConnection.twitchName}'")
                        info(" verbunden.")
                    }

                    success(" (${System.currentTimeMillis() - start}ms)")
                }
            }
        }
    }
}