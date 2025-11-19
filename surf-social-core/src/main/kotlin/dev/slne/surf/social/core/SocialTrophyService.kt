package dev.slne.surf.social.core

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
}