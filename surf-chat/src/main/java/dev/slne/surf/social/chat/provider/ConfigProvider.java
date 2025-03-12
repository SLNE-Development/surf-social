package dev.slne.surf.social.chat.provider;

import dev.slne.surf.social.chat.SurfChat;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

@Getter
public class ConfigProvider {
  @Getter
  private static final ConfigProvider instance = new ConfigProvider();
  private final FileConfiguration config = SurfChat.getInstance().getConfig();

  private String privateMessageFormatSend;
  private String privateMessageFormatReceive;
  private String publicMessageFormat;

  public void reload() {
    privateMessageFormatSend = config.getString("private-message-format-send");
    privateMessageFormatReceive = config.getString("private-message-format-receive");
    publicMessageFormat = config.getString("public-message-format");
  }
}
