package dev.slne.surf.social.microservice.repository

import com.github.benmanes.caffeine.cache.Caffeine
import com.sksamuel.aedile.core.asLoadingCache
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.eq
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.selectAll
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import dev.slne.surf.social.api.connection.impl.DiscordConnection
import dev.slne.surf.social.api.connection.impl.TwitchConnection
import dev.slne.surf.social.microservice.table.SocialConnectionsTable
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.TimeUnit

object SocialConnectionsRepository {
    private val logger = LoggerFactory.getLogger(SocialConnectionsRepository::class.java)

    private val httpClient = HttpClient(CIO) {
        followRedirects = true
        install(HttpTimeout) {
            requestTimeoutMillis = 10_000
            connectTimeoutMillis = 5_000
            socketTimeoutMillis = 5_000
        }
    }

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
        SocialConnectionsTable.selectAll()
            .where { SocialConnectionsTable.minecraftUuid eq minecraftUuid }
            .firstOrNull()?.get(SocialConnectionsTable.discordUserId)?.let {
                DiscordConnection(
                    discordId = it,
                    discordName = discordNameCache.get(it)
                )
            }
    }

    suspend fun findTwitchConnection(
        minecraftUuid: UUID
    ): TwitchConnection? = suspendTransaction {
        SocialConnectionsTable.selectAll()
            .where { SocialConnectionsTable.minecraftUuid eq minecraftUuid }
            .firstOrNull()
            ?.get(SocialConnectionsTable.twitchId)?.let {
                TwitchConnection(
                    twitchId = it,
                    twitchName = twitchNameCache.get(it)
                )
            }
    }

    private suspend fun fetchDiscordName(discordId: Long): String {
        return try {
            val response =
                httpClient.get("https://discordlookup.mesavirep.xyz/v1/user/${discordId}") {
                    accept(ContentType.Application.Json)
                }

            if (response.status == HttpStatusCode.OK) {
                val jsonObject = json.parseToJsonElement(response.bodyAsText()).jsonObject
                val globalName = jsonObject["global_name"]?.jsonPrimitive?.content
                val username = jsonObject["username"]?.jsonPrimitive?.content

                globalName ?: username ?: discordId.toString()
            } else {
                discordId.toString()
            }
        } catch (e: Exception) {
            logger.warn("Failed to fetch Discord name for id {}: {}", discordId, e.message)
            discordId.toString()
        }
    }

    private suspend fun fetchTwitchName(twitchId: Long): String {
        return try {
            val response = httpClient.get("https://api.ivr.fi/v2/twitch/user") {
                parameter("id", twitchId)
                accept(ContentType.Application.Json)
            }

            if (response.status == HttpStatusCode.OK) {
                val jsonArray = json.parseToJsonElement(response.bodyAsText())

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