package dev.slne.surf.social.microservice.table

import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.Table
import java.util.*

object AccountsTable : Table("public.account") {
    val userId = text("userId").transform(
        wrap = { UUID.fromString(it) },
        unwrap = UUID::toString
    )

    val provider = text("provider")
    val providerAccountId = text("providerAccountId")

    override val primaryKey = PrimaryKey(provider, providerAccountId)
}