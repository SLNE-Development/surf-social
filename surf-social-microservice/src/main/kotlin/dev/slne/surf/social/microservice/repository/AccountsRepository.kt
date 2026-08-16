package dev.slne.surf.social.microservice.repository

import com.github.benmanes.caffeine.cache.Caffeine
import com.sksamuel.aedile.core.asLoadingCache
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.and
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.eq
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.deleteWhere
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.select
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import dev.slne.surf.social.api.connection.impl.DiscordConnection
import dev.slne.surf.social.api.connection.impl.TwitchConnection
import dev.slne.surf.social.microservice.config.SocialConfig
import dev.slne.surf.social.microservice.table.AccountsTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
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
    ): DiscordConnection? = suspendTransaction {
        val minecraftAccountId = AccountsTable
            .select(AccountsTable.userId, AccountsTable.provider, AccountsTable.providerAccountId)
            .where((AccountsTable.provider eq "minecraft") and (AccountsTable.providerAccountId eq minecraftUuid.toString()))
            .firstOrNull()?.getOrNull(AccountsTable.userId) ?: return@suspendTransaction null


        val discordId = AccountsTable
            .select(AccountsTable.userId, AccountsTable.provider, AccountsTable.providerAccountId)
            .where((AccountsTable.provider eq "discord") and (AccountsTable.userId eq minecraftAccountId))
            .firstOrNull()?.getOrNull(AccountsTable.providerAccountId)?.toLongOrNull()
            ?: return@suspendTransaction null


        return@suspendTransaction DiscordConnection(
            discordId = discordId,
            discordName = discordNameCache.get(discordId)
        )
    }

    suspend fun findTwitchConnection(
        minecraftUuid: UUID
    ): TwitchConnection? = suspendTransaction {
        val minecraftProviderAccountId = AccountsTable
            .select(AccountsTable.userId, AccountsTable.provider, AccountsTable.providerAccountId)
            .where((AccountsTable.provider eq "minecraft") and (AccountsTable.providerAccountId eq minecraftUuid.toString()))
            .firstOrNull()?.getOrNull(AccountsTable.userId) ?: return@suspendTransaction null

        val twitchId = AccountsTable
            .select(AccountsTable.userId, AccountsTable.provider, AccountsTable.providerAccountId)
            .where((AccountsTable.provider eq "twitch") and (AccountsTable.userId eq minecraftProviderAccountId))
            .firstOrNull()?.getOrNull(AccountsTable.providerAccountId)?.toLongOrNull()
            ?: return@suspendTransaction null

        return@suspendTransaction TwitchConnection(
            twitchId = twitchId,
            twitchName = twitchNameCache.get(twitchId)
        )
    }

    suspend fun unlinkMinecraftAccount(minecraftUuid: UUID) = suspendTransaction {
        AccountsTable.deleteWhere { (AccountsTable.provider eq "minecraft") and (AccountsTable.providerAccountId eq minecraftUuid.toString()) } > 0
    }

    private suspend fun fetchDiscordName(discordId: Long): String {
        return try {
            val request = HttpRequest.newBuilder()
                .uri(URI.create("https://discord.com/api/users/${discordId}"))
                .header("Authorization", "Bot ${SocialConfig.discordBotToken}")
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build()

            val response = withContext(Dispatchers.IO) {
                httpClient.send(request, HttpResponse.BodyHandlers.ofString())
            }

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

            val response = withContext(Dispatchers.IO) {
                httpClient.send(request, HttpResponse.BodyHandlers.ofString())
            }

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
        } catch (e: Exception) {
            logger.warn("Failed to fetch Twitch name for id {}: {}", twitchId, e.message)
            twitchId.toString()
        }
    }
}