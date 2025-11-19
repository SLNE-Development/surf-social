package dev.slne.surf.social.bukkit.permission

import dev.slne.surf.surfapi.bukkit.api.permission.PermissionRegistry

object SocialPermissions : PermissionRegistry() {
    const val BASE = "surf.social"

    val COMMAND_LINK = create("$BASE.command.link")
}