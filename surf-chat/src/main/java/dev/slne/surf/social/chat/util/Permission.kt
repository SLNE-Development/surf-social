package dev.slne.surf.social.chat.util

enum class Permission(private val permission: String) {
    CHAT_LIMIT_BYPASS("surf.chat.limit.bypass"),
    CHAT_DISABLE_BYPASS("surf.chat.disable.bypass");

    fun asPermission(): String {
        return permission
    }

    //TODO: Move to SurfChatPermissions.kt
}