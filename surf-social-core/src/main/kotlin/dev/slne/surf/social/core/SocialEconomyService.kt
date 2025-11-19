package dev.slne.surf.social.core

import dev.slne.surf.social.api.connection.SocialConnection
import it.unimi.dsi.fastutil.objects.ObjectSet
import java.util.UUID

interface SocialEconomyService {
    fun getBalances(minecraftUuid: UUID): SocialBalanceData

    data class SocialBalanceData(
        val minecraftUuid: UUID,
        val balances: Map<String, Double>
    )
}