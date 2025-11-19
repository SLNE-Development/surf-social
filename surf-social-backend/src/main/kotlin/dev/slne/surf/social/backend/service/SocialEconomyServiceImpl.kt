package dev.slne.surf.social.backend.service

import com.google.auto.service.AutoService
import dev.slne.surf.social.core.SocialEconomyService
import net.kyori.adventure.util.Services
import java.util.*

@AutoService(SocialEconomyService::class)
class SocialEconomyServiceImpl : SocialEconomyService, Services.Fallback {
    override fun getBalances(minecraftUuid: UUID): SocialEconomyService.SocialBalanceData {
        return SocialEconomyService.SocialBalanceData(
            minecraftUuid = minecraftUuid,
            balances = mapOf(
                "CC" to 1500.0,
                "EC" to 250.5
            )
        )
    }
}