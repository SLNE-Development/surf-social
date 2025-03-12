package dev.slne.surf.social.chat.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.slne.surf.social.chat.service.ChatHistoryService;
import org.bukkit.Bukkit;

public class SurfChatDeleteCommand extends CommandAPICommand {

  public SurfChatDeleteCommand(String commandName) {
    super(commandName);

    withPermission("surf.chat.command.surf-chat.delete");
    withArguments(new IntegerArgument("messageID"));
    executesPlayer((player, args) -> {
      Integer messageID = args.getUnchecked("messageID");

      Bukkit.getOnlinePlayers().forEach(onlinePlayer -> {
        ChatHistoryService.getInstance().removeMessage(onlinePlayer.getUniqueId(), messageID);
        ChatHistoryService.getInstance().resend(onlinePlayer.getUniqueId());
      });
    });
  }
}
