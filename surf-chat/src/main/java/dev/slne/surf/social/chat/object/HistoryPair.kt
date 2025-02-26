package dev.slne.surf.social.chat.`object`

import java.util.UUID

data class HistoryPair(
    val messageID: UUID,
    val sendTime: Long
)

