package dev.slne.surf.social.chat.history

import com.google.gson.Gson
import java.util.*


class SentMessage(val message: String, val timestamp: Long, val type: MessageType) {

    fun serialize(): String {
        return Gson().toJson(this, SentMessage::class.java)
    }

    val date: Date
        get() = Date(timestamp * 1000)

    companion object {
        fun deserialize(json: String?): SentMessage {
            return Gson().fromJson(json, SentMessage::class.java)
        }
    }
}