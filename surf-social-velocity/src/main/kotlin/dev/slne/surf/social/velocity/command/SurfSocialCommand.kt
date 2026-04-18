package dev.slne.surf.social.velocity.command

import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.literalArgument
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.social.velocity.permission.SocialPermissions
import dev.slne.surf.social.velocity.plugin

fun surfSocialCommand() = commandTree("surfsocial") {
    withPermission(SocialPermissions.COMMAND_SURF_SOCIAL)

    literalArgument("reload") {
        anyExecutor { source, _ ->
            plugin.socialConfigManager.reload()

            source.sendText {
                appendSuccessPrefix()
                success("Die Config wurde neu geladen.")
            }
        }
    }
}