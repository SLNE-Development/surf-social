package dev.slne.surf.social.microservice.repository

import com.github.benmanes.caffeine.cache.Caffeine
import com.sksamuel.aedile.core.asLoadingCache
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.selectAll
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import dev.slne.surf.social.api.connection.SocialConnection
import dev.slne.surf.social.api.connection.impl.DiscordConnection
import dev.slne.surf.social.api.connection.impl.TwitchConnection
import dev.slne.surf.social.microservice.table.SocialConnectionsTable
import kotlinx.coroutines.flow.firstOrNull
import java.util.*
import java.util.concurrent.TimeUnit

object SocialConnectionsRepository {
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

    // TODO: Integrate with Discord API to resolve actual usernames
    private suspend fun fetchDiscordName(discordId: Long): String {
        return discordId.toString()
    }

    // TODO: Integrate with Twitch API to resolve actual usernames
    private suspend fun fetchTwitchName(twitchId: Long): String {
        return twitchId.toString()
    }
}