package dev.slne.surf.social.core

import it.unimi.dsi.fastutil.objects.ObjectSet
import java.util.UUID

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
}