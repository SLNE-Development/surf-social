package dev.slne.surf.social.core

import dev.slne.surf.surfapi.core.api.util.requiredService
import java.util.*

interface SocialPlaytimeService {
    suspend fun getPlaytime(minecraftUuid: UUID): SocialPlaytimeData

    data class SocialPlaytimeData(
        val minecraftUuid: UUID,
        val summedPlaytime: Int,
        val firstPlayed: Long,
        val playtime: Map<String, Int>
    )

    companion object {
        val INSTANCE = requiredService<SocialPlaytimeService>()
    }
}

val socialPlaytimeService get() = SocialPlaytimeService.INSTANCE