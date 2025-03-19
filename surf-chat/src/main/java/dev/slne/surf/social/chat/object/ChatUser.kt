package dev.slne.surf.social.chat.`object`

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.shynixn.mccoroutine.folia.launch
import com.google.gson.Gson
import com.sksamuel.aedile.core.asLoadingCache
import com.sksamuel.aedile.core.expireAfterAccess
import com.sksamuel.aedile.core.withRemovalListener
import dev.slne.surf.social.chat.SurfChat
import dev.slne.surf.social.chat.history.SentMessage
import dev.slne.surf.social.chat.service.DatabaseService
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import io.ktor.client.plugins.*
import it.unimi.dsi.fastutil.objects.ObjectSet
import java.util.*
import kotlin.time.Duration.Companion.minutes


class ChatUser(
    val uuid: UUID,
    var toggledPM: Boolean = false,
    val ignoreList: ObjectSet<UUID> = mutableObjectSetOf(),
    private val sentMessages:MutableList<SentMessage> = mutableListOf()
) {

    fun isIgnoring(target: UUID): Boolean {
        return ignoreList.contains(target)
    }

    fun getSentMessages():List<SentMessage>{
        return sentMessages
    }

    fun addSentMessage(message: SentMessage){
        sentMessages.add(message)
    }
    fun serializeSentMessages():String{
        return Gson().toJson(sentMessages)
    }

    companion object {
        val cache = Caffeine.newBuilder()
            .expireAfterAccess(30.minutes)
            .withRemovalListener { _, user, _ -> DatabaseService.saveUser(user as ChatUser) }
            .asLoadingCache<UUID, ChatUser> { DatabaseService.loadUser(it) }

        suspend fun getUser(uuid: UUID): ChatUser {
            return this.cache.get(uuid)
        }

        fun saveSentMessage(player:UUID,msg:SentMessage){
            SurfChat.instance.launch {
                getUser(player).addSentMessage(msg)
            }
        }

        fun deserializeSentMessagesList(json:String):MutableList<SentMessage>{
            val test = Gson().fromJson(
                json,
                Array<SentMessage>::class.java)
            val list = mutableListOf<SentMessage>()
            if (test != null) list.addAll(test)
            return list
        }
    }
}