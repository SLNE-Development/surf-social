package dev.slne.surf.friends.core.util;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.ConsoleCommandSender;

@Getter
public class FriendLogger {
  public ConsoleCommandSender sender;

  public Component prefix = Component.text("[SurfSocial/SurfFriends] ").color(NamedTextColor.WHITE);

  public void error(String message){
    sender.sendMessage(prefix.append(Component.text(message).color(NamedTextColor.RED)));
  }

  public void error(Exception e){
    sender.sendMessage(prefix.append(Component.text(e.getMessage()).color(NamedTextColor.RED)));
  }

  public void info(String message){
    sender.sendMessage(prefix.append(Component.text(message).color(NamedTextColor.GREEN)));
  }

  public void warn(String message){
    sender.sendMessage(prefix.append(Component.text(message).color(NamedTextColor.YELLOW)));
  }
}
