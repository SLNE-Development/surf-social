package dev.slne.surf.social.chat.permission

import dev.slne.surf.surfapi.bukkit.api.permission.PermissionRegistry

object SurfChatPermissions: PermissionRegistry() {
    val deletePerms = create("surf.chat.delete")
    val teleportPerms = create("surf.chat.teleport")
}