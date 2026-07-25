package dev.slne.surf.social.velocity.command

import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.api.core.command.args.awaiting
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.api.velocity.command.executors.anyExecutorSuspend
import dev.slne.surf.api.velocity.command.executors.playerExecutorSuspend
import dev.slne.surf.core.api.common.player.SurfPlayer
import dev.slne.surf.core.api.velocity.command.argument.surfOfflinePlayerArgument
import dev.slne.surf.social.api.SurfSocialApi
import dev.slne.surf.social.api.connection.impl.DiscordConnection
import dev.slne.surf.social.api.connection.impl.TwitchConnection
import dev.slne.surf.social.api.findConnection
import dev.slne.surf.social.velocity.config
import dev.slne.surf.social.velocity.permission.SocialPermissions
import dev.slne.surf.social.velocity.util.encryptUuid
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import java.util.*

fun linkCommand() = commandTree("link") {
    withPermission(SocialPermissions.COMMAND_LINK)

    stringArgument("code") {
        playerExecutorSuspend { player, arguments ->
            val code: String by arguments

            if (respondMinecraftAuth(player.uniqueId, code)) {
                player.sendText {
                    appendSuccessPrefix()
                    success("Dein Minecraft Account wurde erfolgreich verifiziert.")
                }
            } else {
                player.sendText {
                    appendErrorPrefix()
                    error("Es ist ein Fehler aufgetreten. Bitte versuche es später erneut.")
                }
            }
        }
    }

    literalArgument("twitch") {
        playerExecutor { player, _ ->
            player.sendText {
                val url = "https://id.twitch.tv/oauth2/authorize" +
                        "?client_id=${config.twitchClientId}" +
                        "&redirect_uri=https://server.castcrafter.de/api/auth/twitch/callback" +
                        "&response_type=code" +
                        "&state=${encryptUuid(player.uniqueId)}"

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
                val url = "https://discord.com/oauth2/authorize" +
                        "?client_id=${config.discordClientId}" +
                        "&redirect_uri=https://server.castcrafter.de/api/auth/discord/callback" +
                        "&response_type=code" +
                        "&scope=identify" +
                        "&state=${encryptUuid(player.uniqueId)}"

                appendInfoPrefix()
                info("Bitte verifiziere deinen Discord Account hier: ")
                append {
                    spacer("[")
                    variableValue("Verifizierungslink")
                    spacer("]")
                    clickOpensUrl(url)
                }
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

private val client = HttpClient {
    install(ContentNegotiation) {
        json()
    }
}

private suspend fun respondMinecraftAuth(playerUuid: UUID, code: String) = client.post {
    url("https://auth.castcrafter.de/api/minecraft-link")
    contentType(ContentType.Application.Json)
    bearerAuth(config.minecraftAuthToken)

    userAgent("surf-social-velocity/LinkCommand")

    setBody(
        """
            {
                "minecraftUuid": "$playerUuid",
                "code": "$code"
            }
            """.trimIndent()
    )
}.status.isSuccess()