package dev.slne.surf.social.chat.service

import dev.slne.surf.social.chat.SurfChat
import dev.slne.surf.social.chat.history.MessageType
import dev.slne.surf.social.chat.history.SentMessage
import dev.slne.surf.social.chat.`object`.ChatUser
import dev.slne.surf.social.chat.permission.SurfChatPermissions
import dev.slne.surf.social.chat.provider.ConfigurationProvider
import dev.slne.surf.social.chat.util.MessageBuilder
import dev.slne.surf.surfapi.core.api.util.mutableObject2LongMapOf
import dev.slne.surf.surfapi.core.api.util.mutableObject2MultiObjectsMapOf
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object ChatLimitService {
    private val lastMessages = mutableObject2MultiObjectsMapOf<Player, Long>().apply{ defaultReturnValue(mutableObjectSetOf())}
    private val cooldown = mutableObject2LongMapOf<Player>().apply { defaultReturnValue(0) }
    private val lastPrivateMessages = mutableObject2MultiObjectsMapOf<Player, Long>().apply{ defaultReturnValue(mutableObjectSetOf())}
    private val cooldownDM = mutableObject2LongMapOf<Player>().apply { defaultReturnValue(0) }
    fun validateMessage(player:Player,message:String, isDM:Boolean):Boolean{

        if(this.getCountedPlayers() > ConfigurationProvider.getMinimalPlayersUntilMessageBlock()) {
            SurfChat.send(player, MessageBuilder().error("Der Chat ist momentan deaktiviert."))
            return false
        }

        val cooldown = if (isDM) cooldownDM.getLong(player) else cooldown.getLong(player)
        val lastMSG = if(isDM) lastPrivateMessages[player]!! else lastMessages[player]!!

        if (!player.hasPermission(SurfChatPermissions.chatCooldownBypass)){
            if((System.currentTimeMillis()-cooldown)/50 < getMessageCooldown(isDM)){
                handleSpam(player,message, isDM)
                return false
            }

            for(value in lastMSG)
                if ((System.currentTimeMillis()-value)/50>ConfigurationProvider.getMessageLimitCooldown()) lastMSG.remove(value)

            if (lastMSG.size >= ConfigurationProvider.getMessageLimit()){
                handleSpam(player, message, isDM)
                if (isDM) cooldownDM.put(player, System.currentTimeMillis()) else ChatLimitService.cooldown.put(player, System.currentTimeMillis())
                return false
            }
        }

        lastMSG.add(System.currentTimeMillis())
        return true
    }

    private fun handleSpam(player:Player, message: String, isDM:Boolean){
        SurfChat.send(player, MessageBuilder().error("Bitte mach etwas langsamer."))
        ChatUser.saveSentMessage(player.uniqueId, SentMessage(message, System.currentTimeMillis()/1000, MessageType.Builder.getType(MessageType.BLOCKED_SPAM, isDM)))
    }

    private fun getMessageCooldown(isDM: Boolean):Int{
        return if (isDM) ConfigurationProvider.getPrivateMessageCooldown()
        else ConfigurationProvider.getMessageCooldown()
    }

    private fun getCountedPlayers(): Int {
        return Bukkit.getOnlinePlayers().count {!it.hasPermission(SurfChatPermissions.chatLimitBypass) }
    }
}