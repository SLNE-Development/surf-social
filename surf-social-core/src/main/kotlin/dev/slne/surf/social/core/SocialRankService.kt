package dev.slne.surf.social.core

import it.unimi.dsi.fastutil.objects.ObjectSet
import java.util.UUID
import kotlin.time.Duration

interface SocialRankService {
    suspend fun getRank(minecraftUuid: UUID): SocialRankData

    data class SocialRankData(
        val minecraftUuid: UUID,
        val minecraftName: String,
        val rank: String,
        val rankEndTime: Long?
    )
}