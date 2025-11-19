package dev.slne.surf.social.core

import dev.slne.surf.surfapi.core.api.util.requiredService
import it.unimi.dsi.fastutil.objects.ObjectSet
import java.util.*

interface SocialFriendsService {
    suspend fun getFriends(minecraftUuid: UUID): ObjectSet<SocialFriendData>
    suspend fun getFriendRequests(minecraftUuid: UUID): ObjectSet<SocialFriendRequestData>

    data class SocialFriendData(
        val minecraftUuid: UUID,
        val minecraftName: String,
        val friendUuid: UUID,
        val friendName: String,
        val friendSince: Long
    )

    data class SocialFriendRequestData(
        val minecraftUuid: UUID,
        val minecraftName: String,
        val requesterUuid: UUID,
        val requesterName: String,
        val requestSentAt: Long
    )

    companion object {
        val INSTANCE = requiredService<SocialFriendsService>()
    }
}

val socialFriendsService get() = SocialFriendsService.INSTANCE