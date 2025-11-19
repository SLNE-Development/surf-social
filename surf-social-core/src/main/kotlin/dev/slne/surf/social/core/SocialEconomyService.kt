package dev.slne.surf.social.core

import dev.slne.surf.surfapi.core.api.util.requiredService
import java.util.*

interface SocialEconomyService {
    fun getBalances(minecraftUuid: UUID): SocialBalanceData

    data class SocialBalanceData(
        val minecraftUuid: UUID,
        val balances: Map<String, Double>
    )

    companion object {
        val INSTANCE = requiredService<SocialEconomyService>()
    }
}

val socialEconomyService get() = SocialEconomyService.INSTANCE