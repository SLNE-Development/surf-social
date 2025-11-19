package dev.slne.surf.social.core

import it.unimi.dsi.fastutil.objects.ObjectSet
import java.util.UUID

interface SocialFriendsService {
    suspend fun getFriends(minecraftUuid: UUID): ObjectSet<SocialFriendData>

    data class SocialFriendData(
        val minecraftUuid: UUID,
        val minecraftName: String,
        val friendUuid: UUID,
        val friendName: String,
        val friendSince: Long
    )
}