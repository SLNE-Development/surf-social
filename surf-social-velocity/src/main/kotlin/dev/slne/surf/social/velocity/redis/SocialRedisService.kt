package dev.slne.surf.social.velocity.redis

import dev.slne.surf.redis.RedisApi

object SocialRedisService {
    lateinit var redisApi: RedisApi
        private set

    lateinit var discordRolesPublisher: DiscordRolesPublisher
        private set

    fun connect() {
        redisApi = RedisApi.create()
        redisApi.freezeAndConnect()

        discordRolesPublisher = DiscordRolesPublisher(redisApi)
    }

    fun disconnect() {
        if (::redisApi.isInitialized && redisApi.isConnected()) {
            redisApi.disconnect()
        }
    }
}
