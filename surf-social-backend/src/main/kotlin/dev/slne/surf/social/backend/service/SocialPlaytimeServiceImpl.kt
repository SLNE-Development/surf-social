package dev.slne.surf.social.backend.service

import com.google.auto.service.AutoService
import dev.slne.surf.social.core.SocialPlaytimeService
import net.kyori.adventure.util.Services
import java.util.*

@AutoService(SocialPlaytimeService::class)
class SocialPlaytimeServiceImpl : SocialPlaytimeService, Services.Fallback {
    override suspend fun getPlaytime(minecraftUuid: UUID): SocialPlaytimeService.SocialPlaytimeData {
        return SocialPlaytimeService.SocialPlaytimeData(
            minecraftUuid = minecraftUuid,
            summedPlaytime = 123456,
            firstPlayed = System.currentTimeMillis() - 31536000000L,
            playtime = mapOf(
                "server1" to 65432,
                "server2" to 58024,
                "server3" to 0
            )
        )
    }
}