package dev.slne.surf.social.chat.permission

import dev.slne.surf.surfapi.bukkit.api.permission.PermissionRegistry

object SurfChatPermissions: PermissionRegistry() {
    val deletePerms = create("surf.chat.delete")
    val teleportPerms = create("surf.chat.teleport")
    val chatLimitBypass = create("surf.chat.limit.bypass")
    val chatCooldownBypass = create("surf.chat.cooldown.bypass")
    val blacklistCommand = create("surf.chat.command.blacklist")
    val bypassBlacklist = create("surf.chat.blacklist.bypass")
    val getPunishmentNotification = create("surf.chat.blacklist.receive-punishment-notification")
}