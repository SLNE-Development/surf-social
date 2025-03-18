package dev.slne.surf.social.chat.provider

import dev.slne.surf.social.chat.plugin

object ConfigurationProvider {
    private var PLAYERS_UNTIL_MESSAGE_BLOCK = 50

    fun load() {
        PLAYERS_UNTIL_MESSAGE_BLOCK = plugin.config.getInt("setting.players-until-message-block")
    }

    fun save() {
        plugin.config.set("setting.players-until-message-block", PLAYERS_UNTIL_MESSAGE_BLOCK)

        plugin.saveConfig()
    }

    fun getMinimalPlayersUntilMessageBlock(): Int {
        return PLAYERS_UNTIL_MESSAGE_BLOCK
    }

    fun setMinimalPlayersUntilMessageBlock(value: Int) {
        PLAYERS_UNTIL_MESSAGE_BLOCK = value
    }
}