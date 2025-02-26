package dev.slne.surf.social.chat.util

import dev.slne.surf.surfapi.core.api.messages.Colors
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.minimessage.MiniMessage

class MessageBuilder {
    private var message: Component = Component.empty()

    /**
     * Appends text with the PRIMARY color to the message.
     *
     * @param text the text to be colored
     * @return the MessageBuilder instance
     */
    fun primary(text: String): MessageBuilder {
        message = message.append(Component.text(text, Colors.PRIMARY))
        return this
    }

    /**
     * Appends text with the SECONDARY color to the message.
     *
     * @param text the text to be colored
     * @return the MessageBuilder instance
     */
    fun secondary(text: String): MessageBuilder {
        message = message.append(Component.text(text, Colors.SECONDARY))
        return this
    }

    /**
     * Appends text with the INFO color to the message.
     *
     * @param text the text to be colored
     * @return the MessageBuilder instance
     */
    fun info(text: String): MessageBuilder {
        message = message.append(Component.text(text, Colors.INFO))
        return this
    }

    /**
     * Appends text with the SUCCESS color to the message.
     *
     * @param text the text to be colored
     * @return the MessageBuilder instance
     */
    fun success(text: String): MessageBuilder {
        message = message.append(Component.text(text, Colors.SUCCESS))
        return this
    }

    /**
     * Appends text with the WARNING color to the message.
     *
     * @param text the text to be colored
     * @return the MessageBuilder instance
     */
    fun warning(text: String): MessageBuilder {
        message = message.append(Component.text(text, Colors.WARNING))
        return this
    }

    /**
     * Appends text with the ERROR color to the message.
     *
     * @param text the text to be colored
     * @return the MessageBuilder instance
     */
    fun error(text: String): MessageBuilder {
        message = message.append(Component.text(text, Colors.ERROR))
        return this
    }

    /**
     * Appends text with the VARIABLE_KEY color to the message.
     *
     * @param text the text to be colored
     * @return the MessageBuilder instance
     */
    fun variableKey(text: String): MessageBuilder {
        message = message.append(Component.text(text, Colors.VARIABLE_KEY))
        return this
    }

    /**
     * Appends text with the VARIABLE_VALUE color to the message.
     *
     * @param text the text to be colored
     * @return the MessageBuilder instance
     */
    fun variableValue(text: String): MessageBuilder {
        message = message.append(Component.text(text, Colors.VARIABLE_VALUE))
        return this
    }

    /**
     * Appends text with the PREFIX_COLOR color to the message.
     *
     * @param text the text to be colored
     * @return the MessageBuilder instance
     */
    fun prefixColor(text: String): MessageBuilder {
        message = message.append(Component.text(text, Colors.PREFIX_COLOR))
        return this
    }

    /**
     * Appends text with the DARK_SPACER color to the message.
     *
     * @param text the text to be colored
     * @return the MessageBuilder instance
     */
    fun darkSpacer(text: String): MessageBuilder {
        message = message.append(Component.text(text, Colors.DARK_SPACER))
        return this
    }

    fun miniMessage(text: String): MessageBuilder {
        message = message.append(MiniMessage.miniMessage().deserialize(text))
        return this
    }

    fun component(component: Component): MessageBuilder {
        message = message.append(component)
        return this
    }

    fun command(text: MessageBuilder, hover: MessageBuilder, command: String): MessageBuilder {
        message = message.append(
            text.build().clickEvent(ClickEvent.runCommand(command)).hoverEvent(
                HoverEvent.showText(hover.build())
            )
        )
        return this
    }

    fun suggest(text: MessageBuilder, hover: MessageBuilder, command: String): MessageBuilder {
        message = message.append(
            text.build().clickEvent(ClickEvent.suggestCommand(command)).hoverEvent(
                HoverEvent.showText(hover.build())
            )
        )
        return this
    }

    fun white(text: String): MessageBuilder {
        message = message.append(Component.text(text, Colors.WHITE))
        return this
    }

    fun newLine(): MessageBuilder {
        message = message.append(Component.newline())
        return this
    }

    fun build(): Component {
        return message
    }
}