package dev.slne.surf.social.chat.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.slne.surf.social.chat.service.ChatHistoryService;

public class SurfChatChatClearCommand extends CommandAPICommand {

  public SurfChatChatClearCommand(String commandName) {
    super(commandName);

    withPermission("surf.chat.command.clear");
    executesPlayer((player, args) -> {
      ChatHistoryService.getInstance().clearChat();
    });
  }
}
