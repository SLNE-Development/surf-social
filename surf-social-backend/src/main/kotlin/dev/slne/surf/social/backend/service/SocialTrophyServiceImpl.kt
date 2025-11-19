package dev.slne.surf.social.backend.service

import com.google.auto.service.AutoService
import dev.slne.surf.social.core.SocialTrophyService
import it.unimi.dsi.fastutil.objects.ObjectList
import net.kyori.adventure.util.Services
import java.util.*

@AutoService(SocialTrophyService::class)
class SocialTrophyServiceImpl : SocialTrophyService, Services.Fallback {
    override suspend fun getTrophies(minecraftUuid: UUID): ObjectList<SocialTrophyService.SocialTrophyData> {
        return ObjectList.of(
            SocialTrophyService.SocialTrophyData(
                minecraftUuid = minecraftUuid,
                minecraftName = "TheBjoRedCraft",
                trophyId = "trophy1",
                trophyName = "First Steps",
                trophyDescription = "Awarded for joining the server.",
                awardedAt = System.currentTimeMillis() - 604800000L,
                awardedBy = "NotAmmo"
            ),
            SocialTrophyService.SocialTrophyData(
                minecraftUuid = minecraftUuid,
                minecraftName = "TheBjoRedCraft",
                trophyId = "trophy2",
                trophyName = "Veteran",
                trophyDescription = "Awarded for playing for over 100 hours.",
                awardedAt = System.currentTimeMillis() - 2592000000L,
                awardedBy = "Keviro"
            )
        )
    }
}