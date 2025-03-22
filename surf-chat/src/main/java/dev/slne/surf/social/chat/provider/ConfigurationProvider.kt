package dev.slne.surf.social.chat.provider

import dev.slne.surf.social.chat.plugin

object ConfigurationProvider {
    private var PLAYERS_UNTIL_MESSAGE_BLOCK = 50
    private var TICKS_COOLDOWN_PER_MESSAGE = 200
    private var TICKS_COOLDOWN_PER_PRIVATE_MESSAGE = 200
    private var MESSAGE_LIMIT = 4
    private var MESSAGE_LIMIT_COOLDOWN = 400


    fun load() {
        PLAYERS_UNTIL_MESSAGE_BLOCK = plugin.config.getInt("setting.players-until-message-block")
        TICKS_COOLDOWN_PER_MESSAGE = plugin.config.getInt("setting.chat-limits.cooldown")
        TICKS_COOLDOWN_PER_PRIVATE_MESSAGE = plugin.config.getInt("setting.chat-limits.cooldown-dm")
        MESSAGE_LIMIT = plugin.config.getInt("setting.chat-limits.message-limit")
        MESSAGE_LIMIT_COOLDOWN = plugin.config.getInt("setting.chat-limits.message-limit-cooldown")

    }

    fun save() {
        plugin.config.set("setting.players-until-message-block", PLAYERS_UNTIL_MESSAGE_BLOCK)
        plugin.config.set("setting.chat-limits.cooldown", TICKS_COOLDOWN_PER_MESSAGE)
        plugin.config.set("setting.chat-limits.cooldown-dm", TICKS_COOLDOWN_PER_PRIVATE_MESSAGE)
        plugin.config.set("setting.chat-limits.message-limit", MESSAGE_LIMIT)
        plugin.config.set("setting.chat-limits.message-limit-cooldown", MESSAGE_LIMIT_COOLDOWN)
        plugin.saveConfig()
    }

    fun getMinimalPlayersUntilMessageBlock(): Int {
        return PLAYERS_UNTIL_MESSAGE_BLOCK
    }

    fun setMinimalPlayersUntilMessageBlock(value: Int) {
        PLAYERS_UNTIL_MESSAGE_BLOCK = value
    }

    /**gets the Message Cooldown for Players without permission in ticks
     * @return The value
     */
    fun getMessageCooldown():Int{
        return TICKS_COOLDOWN_PER_MESSAGE
    }
    /**sets the Message Cooldown for Players without permission in ticks
     * @param value the new value in Ticks
     */

    fun setMessageCooldown(value: Int){
        TICKS_COOLDOWN_PER_MESSAGE = value
    }
    fun getPrivateMessageCooldown():Int{
        return TICKS_COOLDOWN_PER_PRIVATE_MESSAGE
    }

    fun setPrivateMessageCooldown(value: Int){
        TICKS_COOLDOWN_PER_PRIVATE_MESSAGE = value
    }
    fun getMessageLimit():Int{
        return MESSAGE_LIMIT
    }

    fun setMessageLimit(value: Int){
        MESSAGE_LIMIT = value
    }

    fun getMessageLimitCooldown():Int{
        return MESSAGE_LIMIT_COOLDOWN
    }

    fun setMessageLimitCooldown(value: Int){
        MESSAGE_LIMIT_COOLDOWN = value
    }

}