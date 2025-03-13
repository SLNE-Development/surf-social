package dev.slne.surf.social.chat.`object`

import net.kyori.adventure.text.Component

data class Message(val sender: String, val receiver: String, val message: Component)
