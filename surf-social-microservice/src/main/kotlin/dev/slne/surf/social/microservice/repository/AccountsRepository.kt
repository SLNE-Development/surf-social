package dev.slne.surf.social.microservice.repository

import com.github.benmanes.caffeine.cache.Caffeine
import com.sksamuel.aedile.core.asLoadingCache
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.Op
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.and
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.eq
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.inSubQuery
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.deleteReturning
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.deleteWhere
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.select
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import dev.slne.surf.social.api.connection.impl.DiscordConnection
import dev.slne.surf.social.api.connection.impl.TwitchConnection
import dev.slne.surf.social.core.common.rabbit.rpc.SocialConnectionRpc
import dev.slne.surf.social.microservice.config.SocialConfig
import dev.slne.surf.social.microservice.table.AccountsTable
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.future.await
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.slf4j.LoggerFactory
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import java.util.*
import java.util.concurrent.TimeUnit

object AccountsRepository {
    private const val PROVIDER_MINECRAFT = "minecraft"
    private const val PROVIDER_DISCORD = "discord"
    private const val PROVIDER_TWITCH = "twitch"

    private val logger = LoggerFactory.getLogger(AccountsRepository::class.java)

    private val httpClient = HttpClient.newBuilder()
        .followRedirects(HttpClient.Redirect.NORMAL)
        .connectTimeout(Duration.ofSeconds(5))
        .build()

    private val json = Json { ignoreUnknownKeys = true }

    private val discordNameCache = Caffeine.newBuilder()
        .maximumSize(10_000)
        .expireAfterWrite(30, TimeUnit.MINUTES)
        .asLoadingCache<Long, String> { fetchDiscordName(it) }

    private val twitchNameCache = Caffeine.newBuilder()
        .maximumSize(10_000)
        .expireAfterWrite(30, TimeUnit.MINUTES)
        .asLoadingCache<Long, String> { fetchTwitchName(it) }

    suspend fun findDiscordConnection(
        minecraftUuid: UUID
    ): DiscordConnection? {
        val discordId = findLinkedProviderAccountId(PROVIDER_DISCORD, minecraftUuid) ?: return null

        return DiscordConnection(
            discordId = discordId,
            discordName = discordNameCache.get(discordId)
        )
    }

    suspend fun findTwitchConnection(
        minecraftUuid: UUID
    ): TwitchConnection? {
        val twitchId = findLinkedProviderAccountId(PROVIDER_TWITCH, minecraftUuid) ?: return null

        return TwitchConnection(
            twitchId = twitchId,
            twitchName = twitchNameCache.get(twitchId)
        )
    }

    suspend fun unlinkMinecraftAccount(minecraftUuid: UUID) = suspendTransaction {
        AccountsTable.deleteWhere { (AccountsTable.provider eq PROVIDER_MINECRAFT) and (AccountsTable.providerAccountId eq minecraftUuid.toString()) } > 0
    }

    suspend fun unlinkMinecraftAccountWithResult(
        minecraftUuid: UUID
    ): SocialConnectionRpc.UnlinkResult = suspendTransaction {
        val deletedMinecraftAccount = AccountsTable
            .deleteReturning(listOf(AccountsTable.userId)) {
                (AccountsTable.provider eq PROVIDER_MINECRAFT) and
                        (AccountsTable.providerAccountId eq minecraftUuid.toString())
            }
            .firstOrNull()
            ?: return@suspendTransaction SocialConnectionRpc.UnlinkResult.NotLinked

        val linkedUserId = deletedMinecraftAccount[AccountsTable.userId]

        val discordId = AccountsTable
            .select(AccountsTable.providerAccountId)
            .where(
                (AccountsTable.provider eq PROVIDER_DISCORD) and
                        (AccountsTable.userId eq linkedUserId)
            )
            .limit(1)
            .firstOrNull()
            ?.getOrNull(AccountsTable.providerAccountId)
            ?.toLongOrNull()

        SocialConnectionRpc.UnlinkResult.Unlinked(discordId)
    }

    /**
     * Resolves the account id that [provider] has linked to [minecraftUuid], in a single round trip.
     */
    private suspend fun findLinkedProviderAccountId(
        provider: String,
        minecraftUuid: UUID
    ): Long? = suspendTransaction {
        AccountsTable
            .select(AccountsTable.providerAccountId)
            .where(linkedAccountCondition(provider, minecraftUuid))
            .limit(1)
            .firstOrNull()
            ?.getOrNull(AccountsTable.providerAccountId)
            ?.toLongOrNull()
    }

    private fun linkedAccountCondition(provider: String, minecraftUuid: UUID): Op<Boolean> =
        (AccountsTable.provider eq provider) and
                AccountsTable.userId.inSubQuery(
                    AccountsTable
                        .select(AccountsTable.userId)
                        .where(
                            (AccountsTable.provider eq PROVIDER_MINECRAFT) and
                                    (AccountsTable.providerAccountId eq minecraftUuid.toString())
                        )
                )

    private suspend fun fetchDiscordName(discordId: Long): String {
        return try {
            val request = HttpRequest.newBuilder()
                .uri(URI.create("https://discord.com/api/users/${discordId}"))
                .header("Authorization", "Bot ${SocialConfig.discordBotToken}")
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build()

            val response = httpClient
                .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .await()

            if (response.statusCode() == 200) {
                val jsonObject = json.parseToJsonElement(response.body()).jsonObject
                val globalName = jsonObject["global_name"]?.jsonPrimitive?.content
                val username = jsonObject["username"]?.jsonPrimitive?.content

                globalName ?: username ?: discordId.toString()
            } else {
                logger.warn(
                    "Discord API returned status {} for user {}",
                    response.statusCode(),
                    discordId
                )
                discordId.toString()
            }
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (e: Exception) {
            logger.warn("Failed to fetch Discord name for id {}: {}", discordId, e.message)
            discordId.toString()
        }
    }

    private suspend fun fetchTwitchName(twitchId: Long): String {
        return try {
            val request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.ivr.fi/v2/twitch/user?id=${twitchId}"))
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build()

            val response = httpClient
                .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .await()

            if (response.statusCode() == 200) {
                val jsonArray = json.parseToJsonElement(response.body())

                if (jsonArray is JsonArray && jsonArray.isNotEmpty()) {
                    val userObject = jsonArray[0].jsonObject
                    val displayName = userObject["displayName"]?.jsonPrimitive?.content
                    val login = userObject["login"]?.jsonPrimitive?.content

                    displayName ?: login ?: twitchId.toString()
                } else {
                    twitchId.toString()
                }
            } else {
                twitchId.toString()
            }
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (e: Exception) {
            logger.warn("Failed to fetch Twitch name for id {}: {}", twitchId, e.message)
            twitchId.toString()
        }
    }
}
