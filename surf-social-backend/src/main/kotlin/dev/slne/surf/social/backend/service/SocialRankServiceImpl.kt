package dev.slne.surf.social.backend.service

import com.google.auto.service.AutoService
import dev.slne.surf.social.core.SocialRankService
import net.kyori.adventure.util.Services
import java.util.*

@AutoService(SocialRankService::class)
class SocialRankServiceImpl : SocialRankService, Services.Fallback {
    override suspend fun getRank(minecraftUuid: UUID): SocialRankService.SocialRankData {
        return SocialRankService.SocialRankData(
            minecraftUuid = minecraftUuid,
            minecraftName = "TheBjoRedCraft",
            rank = "<red>Developer",
            rankEndTime = null
        )
    }
}