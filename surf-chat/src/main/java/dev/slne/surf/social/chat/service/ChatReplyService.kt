package dev.slne.surf.social.chat.service

import com.github.benmanes.caffeine.cache.Caffeine
import java.util.*

object ChatReplyService {
    private val cache = Caffeine.newBuilder()
        .maximumSize(1000)
        .build<UUID, UUID>()

    fun get(player: UUID): UUID? {
        return cache.getIfPresent(player)
    }

    fun clear(player: UUID) {
        cache.invalidate(player)
    }
}