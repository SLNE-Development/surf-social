package dev.slne.surf.social.chat.command;

import dev.jorel.commandapi.CommandAPICommand;

public class SurfChatCommand extends CommandAPICommand {

  public SurfChatCommand(String commandName) {
    super(commandName);

    withPermission("surf.chat.command.surf-chat");
    withSubcommand(new SurfChatDeleteCommand("delete"));
    withSubcommand(new SurfChatChatClearCommand("clear"));
  }
}
