package dev.slne.surf.social.chat.command.channel;

import dev.jorel.commandapi.CommandAPICommand;

public class ChannelCommand extends CommandAPICommand {

  public ChannelCommand(String commandName) {
    super(commandName);

    withPermission("surf.chat.command.channel");
    withSubcommand(new ChannelAcceptInviteCommand("accept"));
    withSubcommand(new ChannelCreateCommand("create"));
    withSubcommand(new ChannelDeleteCommand("delete"));
    withSubcommand(new ChannelInviteCommand("invite"));
    withSubcommand(new ChannelInviteRevokeCommand("revoke"));
    withSubcommand(new ChannelInfoCommand("info"));
    withSubcommand(new ChannelMembersCommand("members"));
    withSubcommand(new ChannelListCommand("list"));
    withSubcommand(new ChannelJoinCommand("join"));
    withSubcommand(new ChannelLeaveCommand("leave"));
    withSubcommand(new ChannelStateToggleCommand("toggleState"));
    withSubcommand(new ChannelBanCommand("ban"));
    withSubcommand(new ChannelUnBanCommand("unban"));
    withSubcommand(new ChannelDemoteCommand("demote"));
    withSubcommand(new ChannelPromoteCommand("promote"));
    withSubcommand(new ChannelKickCommand("kick"));
    withSubcommand(new ChannelMoveCommand("move"));
    withSubcommand(new ChannelForceDeleteCommand("forceDelete"));
    withSubcommand(new ChannelForceJoinCommand("forceJoin"));
    withSubcommand(new ChannelTransferOwnerShipCommand("transferOwnership"));
  }
}
