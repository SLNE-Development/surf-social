package dev.slne.surf.social.core

import java.util.UUID

interface SocialPlaytimeService {
    suspend fun getPlaytime(minecraftUuid: UUID): SocialPlaytimeData

    data class SocialPlaytimeData(
        val minecraftUuid: UUID,
        val summedPlaytime: Int,
        val firstPlayed: Long,
        val playtime: Map<String, Int>
    )
}