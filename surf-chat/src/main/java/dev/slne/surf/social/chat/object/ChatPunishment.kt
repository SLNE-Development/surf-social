package dev.slne.surf.social.chat.`object`

import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import dev.slne.surf.social.chat.provider.ConfigurationProvider
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class ChatPunishment (
    @Expose private var punishment: Punishment = Punishment.BLOCK,
    @Expose @Nullable private var time:Int? = null,
    @Expose private var message:String? = null
){
    override fun toString(): String {
        if (punishment == Punishment.BLOCK && time == null && message == null) return ""
        return gson.toJson(this)
    }

    fun asString():String{return toString()}

    fun getMessage():String{
        val msg = if (getRawMessage() != null) getRawMessage()
        else ConfigurationProvider.getDefaultPunishment(punishment).getRawMessage()
        if (msg != null){
            return msg.replace("%time", getTimeText(), true)
        }else throw RuntimeException("No available default message for $punishment")
    }

    fun getRawMessage():String?{return message}

    fun getTime():Duration{
        if (time == null){
            val cp = ConfigurationProvider.getDefaultPunishment(punishment)
            return if (cp != this) cp.getTime() else Duration.ZERO
        }
        return time!!.toDuration(DurationUnit.SECONDS)
    }

    fun getJavaTime():java.time.Duration{
        if (time == null) return ConfigurationProvider.getDefaultPunishment(punishment).getJavaTime()
        return java.time.Duration.ofSeconds(time!!.toLong())
    }

    fun getTimeText():String{
        getTime().toComponents { days, hours, minutes, seconds, _ ->
            var s = ""
            if (days > 0) s = s + days+"d "
            if (hours > 0) s = s + hours+"h "
            if (minutes > 0) s = s + minutes+"m "
            if (seconds > 0) s = s + seconds+"s "
            return s
        }
    }

    fun getCreationMessage(word:String):String{
        var duration = ""
        if (time != null){
            if (getTimeText().isNotBlank()) duration = ", Dauer: ${getTimeText()}"
        }
        return "Wort \"$word\" zur Blacklist hinzugefügt (Bestrafung: ${punishment.text}${duration}, Nachricht: ${getMessage()})"

    }

    fun getUpdateMessage(word: String):String{
        return getCreationMessage(word).replace("zur Blacklist hinzugefügt", "geupdatet")
    }

    fun getInfoMessage(word: String):String{
        return getCreationMessage(word).replace(" zur Blacklist hinzugefügt", ":")
    }

    fun getPunishment():Punishment{
        return punishment
    }

    enum class Punishment(val text:String){
        BLOCK("Chat-Benachrichtigung"),MUTE("Stumm schalten"),KICK("Kick"),BAN("Ban");

        operator fun component1(): String {
            return this.text
        }
    }

    companion object {
        val gson: Gson = GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()

        fun buildFromArgs(args:String):@NotNull ChatPunishment{
            var finalArgs = ChatPunishment()
            if (args.isNotBlank()){
                val type = object : TypeToken<ChatPunishment>(){}.type
                finalArgs = gson.fromJson(args, type)
            }
            if (finalArgs.time == 0) finalArgs.time = null
            return finalArgs
        }
        fun getDefaultPunishments():HashMap<Punishment, ChatPunishment>{
            val DEFAULT_PUNISHMENTS = HashMap<Punishment, ChatPunishment>()
            DEFAULT_PUNISHMENTS[Punishment.MUTE] =
                ChatPunishment(Punishment.MUTE, 600, "Bitte überdenke deine Ausdrucksweise und nimm in %time wieder am Chat teil.")
            DEFAULT_PUNISHMENTS[Punishment.BAN] =
                ChatPunishment(Punishment.BAN, 3600, "Bitte überdenke deine Ausdrucksweise und komm in %time wieder zurück.")
            DEFAULT_PUNISHMENTS[Punishment.KICK] =
                ChatPunishment(Punishment.KICK, 0, "Bitte überdenke deine Ausdrucksweise und komm dann wieder zurück, wenn du dich in der Lage fühlst, angemessene Unterhaltungen zu führen.")
            DEFAULT_PUNISHMENTS[Punishment.BLOCK] =
                ChatPunishment(Punishment.BLOCK, 0, "Bitte achte auf deine Ausdrucksweise!")
            return DEFAULT_PUNISHMENTS
        }
    }
}
