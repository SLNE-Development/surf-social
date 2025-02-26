package dev.slne.surf.social.chat.command.channel

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.subcommand

class ChannelCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission("surf.chat.command.channel")
        subcommand(ChannelAcceptInviteCommand("accept"))
        subcommand(ChannelCreateCommand("create"))
        subcommand(ChannelDeleteCommand("delete"))
        subcommand(ChannelInviteCommand("invite"))
        subcommand(ChannelInviteRevokeCommand("revoke"))
        subcommand(ChannelInfoCommand("info"))
        subcommand(ChannelMembersCommand("members"))
        subcommand(ChannelListCommand("list"))
        subcommand(ChannelJoinCommand("join"))
        subcommand(ChannelLeaveCommand("leave"))
        subcommand(ChannelStateToggleCommand("toggleState"))
        subcommand(ChannelBanCommand("ban"))
        subcommand(ChannelUnBanCommand("unban"))
        subcommand(ChannelDemoteCommand("demote"))
        subcommand(ChannelPromoteCommand("promote"))
        subcommand(ChannelKickCommand("kick"))
        subcommand(ChannelMoveCommand("move"))
        subcommand(ChannelForceDeleteCommand("forceDelete"))
        subcommand(ChannelForceJoinCommand("forceJoin"))
        subcommand(ChannelTransferOwnerShipCommand("transferOwnership"))
    }
}
