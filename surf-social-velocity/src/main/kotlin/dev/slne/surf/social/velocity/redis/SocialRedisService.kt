package dev.slne.surf.social.velocity.redis

import dev.slne.surf.redis.RedisApi
import java.util.concurrent.atomic.AtomicReference

object SocialRedisService {

    /**
     * The api and its publisher are held together in one immutable value so that a reader can
     * never observe a connected [RedisApi] with a publisher that is still missing.
     */
    private class Connection(
        val redisApi: RedisApi,
        val discordRolesPublisher: DiscordRolesPublisher
    )

    private val connection = AtomicReference<Connection?>()

    private val current
        get() = connection.get() ?: error("SocialRedisService has not been connected yet")

    val redisApi: RedisApi get() = current.redisApi

    val discordRolesPublisher: DiscordRolesPublisher get() = current.discordRolesPublisher

    fun connect() {
        val redisApi = RedisApi.create()
        redisApi.freezeAndConnect()

        connection.set(Connection(redisApi, DiscordRolesPublisher(redisApi)))
    }

    fun disconnect() {
        val active = connection.getAndSet(null) ?: return

        if (active.redisApi.isConnected()) {
            active.redisApi.disconnect()
        }
    }
}
