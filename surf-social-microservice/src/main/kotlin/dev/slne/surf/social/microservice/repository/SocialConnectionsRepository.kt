package dev.slne.surf.social.microservice.repository

import com.github.benmanes.caffeine.cache.Caffeine
import com.sksamuel.aedile.core.asLoadingCache
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.selectAll
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import dev.slne.surf.social.api.connection.SocialConnection
import dev.slne.surf.social.api.connection.impl.DiscordConnection
import dev.slne.surf.social.api.connection.impl.TwitchConnection
import dev.slne.surf.social.microservice.table.SocialConnectionsTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.*
import java.util.concurrent.TimeUnit

object SocialConnectionsRepository {
    private val httpClient = HttpClient.newBuilder()
        .followRedirects(HttpClient.Redirect.NORMAL)
        .build()

    private val json = Json { ignoreUnknownKeys = true }

    private val discordNameCache = Caffeine.newBuilder()
        .maximumSize(10_000)
        .expireAfterWrite(10, TimeUnit.MINUTES)
        .asLoadingCache<Long, String> { fetchDiscordName(it) }

    private val twitchNameCache = Caffeine.newBuilder()
        .maximumSize(10_000)
        .expireAfterWrite(10, TimeUnit.MINUTES)
        .asLoadingCache<Long, String> { fetchTwitchName(it) }

    suspend fun findDiscordConnection(
        minecraftUuid: UUID
    ): SocialConnection? = suspendTransaction {
        SocialConnectionsTable.selectAll()
            .where { SocialConnectionsTable.minecraftUuid eq minecraftUuid }
            .firstOrNull()?.let {
                DiscordConnection(
                    discordId = it[SocialConnectionsTable.discordUserId],
                    discordName = discordNameCache.get(it[SocialConnectionsTable.discordUserId])
                )
            }
    }

    suspend fun findTwitchConnection(
        minecraftUuid: UUID
    ): SocialConnection? = suspendTransaction {
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

    private suspend fun fetchDiscordName(discordId: Long): String = withContext(Dispatchers.IO) {
        try {
            val request = HttpRequest.newBuilder()
                .uri(URI.create("https://discordlookup.mesavirep.xyz/v1/user/$discordId"))
                .GET()
                .header("Accept", "application/json")
                .build()

            val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())

            if (response.statusCode() == 200) {
                val jsonObject = json.parseToJsonElement(response.body()).jsonObject
                val globalName = jsonObject["global_name"]?.jsonPrimitive?.content
                val username = jsonObject["username"]?.jsonPrimitive?.content

                globalName ?: username ?: discordId.toString()
            } else {
                discordId.toString()
            }
        } catch (_: Exception) {
            discordId.toString()
        }
    }

    private suspend fun fetchTwitchName(twitchId: Long): String = withContext(Dispatchers.IO) {
        try {
            val request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.ivr.fi/v2/twitch/user?id=$twitchId"))
                .GET()
                .header("Accept", "application/json")
                .build()

            val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())

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
        } catch (_: Exception) {
            twitchId.toString()
        }
    }
}