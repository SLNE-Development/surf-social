package dev.slne.surf.social.paper.permission

import dev.slne.surf.api.paper.permission.PermissionRegistry

object SocialPermissions : PermissionRegistry() {
    const val BASE = "surf.social"

    val COMMAND_LINK = create("$BASE.command.link")
    val COMMAND_LINK_DEBUG = create("$COMMAND_LINK.debug")
}