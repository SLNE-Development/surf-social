package dev.slne.surf.social.chat.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.slne.surf.social.chat.object.Message;
import dev.slne.surf.social.chat.provider.ConfigProvider;
import dev.slne.surf.social.chat.service.ChatHistoryService;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

public class PrivateMessageCommand extends CommandAPICommand {
  public PrivateMessageCommand(String commandName) {
    super(commandName);

    withArguments(new PlayerArgument("player"));
    withArguments(new GreedyStringArgument("message"));
    withAliases("tell", "w", "pm", "dm");
    withPermission("surf.chat.command.private-message");
    executesPlayer((player, args) -> {
      Player target = args.getUnchecked("player");
      String message = args.getUnchecked("message");

      target.sendMessage(MiniMessage.miniMessage().deserialize(PlaceholderAPI.setPlaceholders(player, ConfigProvider.getInstance().getPrivateMessageFormatReceive()).replace("%message%", message)));
      player.sendMessage(MiniMessage.miniMessage().deserialize(PlaceholderAPI.setPlaceholders(target, ConfigProvider.getInstance().getPrivateMessageFormatSend()).replace("%message%", message)));
    });
  }
}
