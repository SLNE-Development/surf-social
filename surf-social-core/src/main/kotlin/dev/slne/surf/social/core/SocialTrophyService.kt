package dev.slne.surf.social.core

import dev.slne.surf.surfapi.core.api.util.requiredService
import it.unimi.dsi.fastutil.objects.ObjectList
import java.util.*

interface SocialTrophyService {
    suspend fun getTrophies(minecraftUuid: UUID): ObjectList<SocialTrophyData>

    data class SocialTrophyData(
        val minecraftUuid: UUID,
        val minecraftName: String,

        val trophyId: String,
        val trophyName: String,
        val trophyDescription: String,
        val awardedAt: Long,
        val awardedBy: String
    )

    companion object {
        val INSTANCE = requiredService<SocialTrophyService>()
    }
}

val socialTrophyService get() = SocialTrophyService.INSTANCE