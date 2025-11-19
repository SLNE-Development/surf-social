package dev.slne.surf.social.backend.service

import com.google.auto.service.AutoService
import dev.slne.surf.social.core.SocialFriendsService
import it.unimi.dsi.fastutil.objects.ObjectSet
import net.kyori.adventure.util.Services
import java.util.*

@AutoService(SocialFriendsService::class)
class SocialFriendsServiceImpl : SocialFriendsService, Services.Fallback {
    override suspend fun getFriends(minecraftUuid: UUID): ObjectSet<SocialFriendsService.SocialFriendData> {
        return ObjectSet.of(
            SocialFriendsService.SocialFriendData(
                minecraftUuid = minecraftUuid,
                minecraftName = "TheBjoRedCraft",
                friendUuid = UUID.fromString("3a1d37a0-0a1d-419f-ac7a-f9201206cfd0"),
                friendName = "Keviro",
                friendSince = System.currentTimeMillis() - 86400000L
            )
        )
    }

    override suspend fun getFriendRequests(minecraftUuid: UUID): ObjectSet<SocialFriendsService.SocialFriendRequestData> {
        return ObjectSet.of(
            SocialFriendsService.SocialFriendRequestData(
                minecraftUuid = minecraftUuid,
                minecraftName = "TheBjoRedCraft",
                requesterUuid = UUID.fromString("5c63e51b-82b1-4222-af0f-66a4c31e36ad"),
                requesterName = "NotAmmo",
                requestSentAt = System.currentTimeMillis() - 43200000L
            )
        )
    }
}