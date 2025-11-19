package dev.slne.surf.social.core

import dev.slne.surf.surfapi.core.api.util.requiredService
import java.util.*

interface SocialRankService {
    suspend fun getRank(minecraftUuid: UUID): SocialRankData

    data class SocialRankData(
        val minecraftUuid: UUID,
        val minecraftName: String,
        val rank: String,
        val rankEndTime: Long?
    )

    companion object {
        val INSTANCE = requiredService<SocialRankService>()
    }
}

val socialRankService get() = SocialRankService.INSTANCE