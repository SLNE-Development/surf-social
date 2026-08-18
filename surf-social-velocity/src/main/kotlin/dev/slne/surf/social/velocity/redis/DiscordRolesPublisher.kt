package dev.slne.surf.social.velocity.redis

import dev.slne.surf.redis.RedisApi
import dev.slne.surf.redis.libs.redisson.api.stream.StreamAddArgs
import dev.slne.surf.redis.libs.redisson.client.codec.StringCodec
import kotlinx.coroutines.future.await
import kotlinx.serialization.Serializable

class DiscordRolesPublisher(private val redisApi: RedisApi) {

    companion object {
        private const val STREAM_FIELD = "request"
        private const val MAX_STREAM_LENGTH = 10_000
    }

    private val stream by lazy {
        redisApi.redisson.getStream<String, String>(
            "surf-discord:discord-roles:whitelisted-roles", // Never change this without a good reason. It is used across multiple services, and changing it will break things.
            StringCodec.INSTANCE
        )
    }

    suspend fun publish(action: Request.Action, discordUserId: Long) {
        val payload = redisApi.json.encodeToString(Request(action, discordUserId))

        stream.addAsync(
            StreamAddArgs.entry(STREAM_FIELD, payload)
                .trimNonStrict()
                .maxLen(MAX_STREAM_LENGTH)
                .noLimit()
        ).await()
    }

    @Serializable
    data class Request(
        val action: Action,
        val userId: Long
    ) {
        enum class Action {
            ADD,
            REMOVE
        }
    }
}
