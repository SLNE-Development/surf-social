package dev.slne.surf.social.chat.`object`

import com.github.benmanes.caffeine.cache.Caffeine
import com.sksamuel.aedile.core.asLoadingCache
import com.sksamuel.aedile.core.expireAfterAccess
import com.sksamuel.aedile.core.withRemovalListener
import dev.slne.surf.social.chat.service.DatabaseService
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import it.unimi.dsi.fastutil.objects.ObjectSet
import java.util.*
import kotlin.time.Duration.Companion.minutes


class ChatUser(
    val uuid: UUID,
    var toggledPM: Boolean = false,
    val ignoreList: ObjectSet<UUID> = mutableObjectSetOf()
) {

    fun isIgnoring(target: UUID): Boolean {
        return ignoreList.contains(target)
    }

    companion object {
        val cache = Caffeine.newBuilder()
            .expireAfterAccess(30.minutes)
            .withRemovalListener { _, user, _ -> DatabaseService.saveUser(user as ChatUser) }
            .asLoadingCache<UUID, ChatUser> { DatabaseService.loadUser(it) }

        suspend fun getUser(uuid: UUID): ChatUser {
            return this.cache.get(uuid)
        }
    }
}