package dev.slne.surf.social.bukkit.command

import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.literalArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.social.bukkit.permission.SocialPermissions

fun linkCommand() = commandTree("link") {
    withPermission(SocialPermissions.COMMAND_LINK)

    literalArgument("twitch") {
        playerExecutor { player, _ ->

        }
    }

    literalArgument("discord") {
        playerExecutor { player, args ->

        }
    }
}