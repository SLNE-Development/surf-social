@file:Suppress("UNCHECKED_CAST")

package dev.slne.surf.social.backend.service

import com.google.auto.service.AutoService
import dev.slne.surf.social.api.connection.SocialConnection
import dev.slne.surf.social.api.connection.impl.DiscordConnection
import dev.slne.surf.social.api.connection.impl.TwitchConnection
import dev.slne.surf.social.core.SocialConnectionsService
import it.unimi.dsi.fastutil.objects.ObjectSet
import net.kyori.adventure.util.Services
import java.util.*

@AutoService(SocialConnectionsService::class)
class SocialConnectionsServiceImpl : SocialConnectionsService, Services.Fallback {
    override fun <P : SocialConnection> getConnection(
        minecraftUuid: UUID
    ): P? {
        return DiscordConnection("TheBjoRedCraft", 955165814011600896) as P
    }

    override fun getConnections(minecraftUuid: UUID): ObjectSet<SocialConnection> {
        return ObjectSet.of(
            DiscordConnection("TheBjoRedCraft", 955165814011600896),
            TwitchConnection("thebjoredcraft", 123456789)
        )
    }
}