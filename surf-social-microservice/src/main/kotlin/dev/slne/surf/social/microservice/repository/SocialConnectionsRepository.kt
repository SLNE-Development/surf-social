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

object SocialConnectionsRepository {
    private val discordNameCache = Caffeine.newBuilder().asLoadingCache<Long, String> {
        fetchDiscordName(it)
    }

    private val twitchNameCache = Caffeine.newBuilder().asLoadingCache<Long, String> {
        fetchTwitchName(it)
    }

    suspend fun findDiscordConnection(
        minecraftUuid: UUID
    ): SocialConnection? = suspendTransaction {
        SocialConnectionsTable.selectAll().firstOrNull {
            it[SocialConnectionsTable.minecraftUuid] == minecraftUuid
        }?.let {
            DiscordConnection(
                discordId = it[SocialConnectionsTable.discordUserId],
                discordName = discordNameCache.get(it[SocialConnectionsTable.discordUserId])
            )
        }
    }

    suspend fun findTwitchConnection(
        minecraftUuid: UUID
    ): SocialConnection? = suspendTransaction {
        SocialConnectionsTable.selectAll().firstOrNull {
            it[SocialConnectionsTable.minecraftUuid] == minecraftUuid
        }?.get(SocialConnectionsTable.twitchId)?.let {
            TwitchConnection(
                twitchId = it,
                twitchName = twitchNameCache.get(it)
            )
        }
    }


    private suspend fun fetchDiscordName(
        discordId: Long
    ): String = TODO("Fetch discord name from discord id")

    private suspend fun fetchTwitchName(
        twitchId: Long
    ): String = TODO("Fetch twitch name from twitch id")
}