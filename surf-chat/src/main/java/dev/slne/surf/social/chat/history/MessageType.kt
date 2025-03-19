package dev.slne.surf.social.chat.history

import net.kyori.adventure.text.format.TextColor

enum class MessageType {
    SENT, SENT_DM,
    BLOCKED_LINK,BLOCKED_WORDS,BLOCKED_SPAM,BLOCKED_INVALID,BLOCKED_MUTED,
    BLOCKED_LINK_DM,BLOCKED_WORDS_DM,BLOCKED_SPAM_DM,BLOCKED_INVALID_DM,BLOCKED_MUTED_DM
    ,BLOCKED_DISABLED_DM,BLOCKED_IGNORING_DM;

    fun isValid():Boolean{
        return when(this){
            SENT_DM -> true
            SENT -> true
            else -> false
        }
    }
    fun getMessage():String{
        var msg = ""
        if (this.name.contains("DM", true)) msg += "DM, "
        if (this.name.contains("SENT", true)) msg += "Gesendet"
        if (this.name.contains("BLOCKED", true)) msg += "Blockiert "
        if (this.name.contains("LINK", true)) msg += "(Verwendung von Links)"
        if (this.name.contains("WORDS", true)) msg += "(Verbotene Wörter)"
        if (this.name.contains("INVALID", true)) msg += "(Verwendung ungültiger Zeichen)"
        if (this.name.contains("MUTED", true)) msg += "(Stumm)"
        return msg
    }
    fun getColor():TextColor{
        return if (isValid()){
            TextColor.color(0, 150, 0)
        }else TextColor.color(150, 0, 50)
    }
    fun isDM():Boolean{
        return this.toString().endsWith("_DM", true)
    }
}