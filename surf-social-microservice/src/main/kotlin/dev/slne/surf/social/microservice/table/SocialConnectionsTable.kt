package dev.slne.surf.social.microservice.table

import dev.slne.surf.database.columns.nativeUuid
import dev.slne.surf.database.table.AuditableLongIdTable

object SocialConnectionsTable : AuditableLongIdTable("social_connections") {
    val discordUserId = long("discord_user_id").uniqueIndex().nullable()
    val minecraftUuid = nativeUuid("minecraft_uuid").uniqueIndex()
    val twitchId = long("twitch_id").uniqueIndex().nullable()
    val blocked = bool("blocked").default(true)
}