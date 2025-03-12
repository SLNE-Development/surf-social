package dev.slne.surf.social.chat.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;

public class MessageBuilder {
  private Component message = Component.empty();
  /**
   * Appends text with the PRIMARY color to the message.
   *
   * @param text the text to be colored
   * @return the MessageBuilder instance
   */
  public MessageBuilder primary(String text) {
    message = message.append(Component.text(text, Colors.PRIMARY));
    return this;
  }

  /**
   * Appends text with the SECONDARY color to the message.
   *
   * @param text the text to be colored
   * @return the MessageBuilder instance
   */
  public MessageBuilder secondary(String text) {
    message = message.append(Component.text(text, Colors.SECONDARY));
    return this;
  }

  /**
   * Appends text with the INFO color to the message.
   *
   * @param text the text to be colored
   * @return the MessageBuilder instance
   */
  public MessageBuilder info(String text) {
    message = message.append(Component.text(text, Colors.INFO));
    return this;
  }

  /**
   * Appends text with the SUCCESS color to the message.
   *
   * @param text the text to be colored
   * @return the MessageBuilder instance
   */
  public MessageBuilder success(String text) {
    message = message.append(Component.text(text, Colors.SUCCESS));
    return this;
  }

  /**
   * Appends text with the WARNING color to the message.
   *
   * @param text the text to be colored
   * @return the MessageBuilder instance
   */
  public MessageBuilder warning(String text) {
    message = message.append(Component.text(text, Colors.WARNING));
    return this;
  }

  /**
   * Appends text with the ERROR color to the message.
   *
   * @param text the text to be colored
   * @return the MessageBuilder instance
   */
  public MessageBuilder error(String text) {
    message = message.append(Component.text(text, Colors.ERROR));
    return this;
  }

  /**
   * Appends text with the VARIABLE_KEY color to the message.
   *
   * @param text the text to be colored
   * @return the MessageBuilder instance
   */
  public MessageBuilder variableKey(String text) {
    message = message.append(Component.text(text, Colors.VARIABLE_KEY));
    return this;
  }

  /**
   * Appends text with the VARIABLE_VALUE color to the message.
   *
   * @param text the text to be colored
   * @return the MessageBuilder instance
   */
  public MessageBuilder variableValue(String text) {
    message = message.append(Component.text(text, Colors.VARIABLE_VALUE));
    return this;
  }

  /**
   * Appends text with the PREFIX_COLOR color to the message.
   *
   * @param text the text to be colored
   * @return the MessageBuilder instance
   */
  public MessageBuilder prefixColor(String text) {
    message = message.append(Component.text(text, Colors.PREFIX_COLOR));
    return this;
  }

  /**
   * Appends text with the DARK_SPACER color to the message.
   *
   * @param text the text to be colored
   * @return the MessageBuilder instance
   */
  public MessageBuilder darkSpacer(String text) {
    message = message.append(Component.text(text, Colors.DARK_SPACER));
    return this;
  }

  public MessageBuilder command(MessageBuilder text, MessageBuilder hover, String command) {
    message = message.append(text.build().clickEvent(ClickEvent.runCommand(command)).hoverEvent(HoverEvent.showText(hover.build())));
    return this;
  }

  public MessageBuilder newLine() {
    message = message.append(Component.newline());
    return this;
  }

  public Component build() {
    return message;
  }
}