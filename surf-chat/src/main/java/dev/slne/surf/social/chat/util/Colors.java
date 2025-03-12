package dev.slne.surf.social.chat.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

/**
 * A class that contains all the colors used in the Surf system. This class is used to provide a
 * uniform appearance across all Surf plugins.
 */
public class Colors {
    // -------------------- Surf Colors -------------------- //

    /**
     * PRIMARY color (#3b92d1). Generally not used in our system. However, an example of its use could
     * be for titles, subtitles, etc.
     */
    public static final TextColor PRIMARY = TextColor.color(0x3b92d1);

    /**
     * SECONDARY color (#5b5b5b). Also seldom used in our system. Could be used for elements like
     * subtitles.
     */
    public static final TextColor SECONDARY = TextColor.color(0x5b5b5b);

    /**
     * INFO color (#40d1db). Used to inform the user about a specific situation. Typically, it's not a
     * follow-up to a user action.
     */
    public static final TextColor INFO = TextColor.color(0x40d1db);

    /**
     * SUCCESS color (#65ff64). Indicates a positive outcome of an action performed by the user.
     */
    public static final TextColor SUCCESS = TextColor.color(0x65ff64);

    /**
     * WARNING color (#f9c353). Used as a direct warning to the user.
     */
    public static final TextColor WARNING = TextColor.color(0xf9c353);

    /**
     * ERROR (or DANGER) color (#ee3d51). Represents error messages directed at the user.
     */
    public static final TextColor ERROR = TextColor.color(0xee3d51);

    /**
     * VARIABLE_KEY color (#3b92d1). Mainly used as a key in listings.
     */
    public static final TextColor VARIABLE_KEY = INFO;

    /**
     * VARIABLE_VALUE color (#f9c353). Primarily used in listings and chat messages as a variable.
     */
    public static final TextColor VARIABLE_VALUE = WARNING;

    /**
     * SPACER color (GRAY). Used for various forms of spacers.
     */
    public static final NamedTextColor SPACER = NamedTextColor.GRAY;

    /**
     * DARK_SPACER color (DARK_GRAY). Used for dark spacers.
     */
    public static final NamedTextColor DARK_SPACER = NamedTextColor.DARK_GRAY;

    /**
     * PREFIX color (#3b92d1). Used for the color of every prefix to provide a uniform appearance.
     */
    public static final TextColor PREFIX_COLOR = PRIMARY;

    // -------------------- Default Colors -------------------- //

    /**
     * The prefix for all Surf plugins
     */
    public static final Component PREFIX = Component.text(">> ", DARK_SPACER)
            .append(Component.text("SC", PREFIX_COLOR))
            .append(Component.text(" | ", DARK_SPACER));

    /**
     * Represents the color black.
     */
    public static final NamedTextColor BLACK = NamedTextColor.BLACK;

    /**
     * Represents the color dark blue.
     */
    public static final NamedTextColor DARK_BLUE = NamedTextColor.DARK_BLUE;

    /**
     * Represents the color dark green.
     */
    public static final NamedTextColor DARK_GREEN = NamedTextColor.DARK_GREEN;

    /**
     * Represents the named text color DARK_AQUA.
     */
    public static final NamedTextColor DARK_AQUA = NamedTextColor.DARK_AQUA;

    /**
     * Represents the dark red color.
     */
    public static final NamedTextColor DARK_RED = NamedTextColor.DARK_RED;

    /**
     * Represents the dark purple named text color.
     */
    public static final NamedTextColor DARK_PURPLE = NamedTextColor.DARK_PURPLE;

    /**
     * The GOLD color for naming text.
     */
    public static final NamedTextColor GOLD = NamedTextColor.GOLD;

    /**
     * Represents the named text color "GRAY".
     */
    public static final NamedTextColor GRAY = NamedTextColor.GRAY;

    /**
     * Represents the named text color "DARK_GRAY".
     */
    public static final NamedTextColor DARK_GRAY = NamedTextColor.DARK_GRAY;

    /**
     * Represents the named text color "BLUE".
     */
    public static final NamedTextColor BLUE = NamedTextColor.BLUE;

    /**
     * Represents the named text color "GREEN".
     */
    public static final NamedTextColor GREEN = NamedTextColor.GREEN;

    /**
     * Represents the named text color "AQUA".
     */
    public static final NamedTextColor AQUA = NamedTextColor.AQUA;

    /**
     * Represents the named text color "RED".
     */
    public static final NamedTextColor RED = NamedTextColor.RED;

    /**
     * Represents the color light purple.
     */
    public static final NamedTextColor LIGHT_PURPLE = NamedTextColor.LIGHT_PURPLE;

    /**
     * Represents the color yellow.
     */
    public static final NamedTextColor YELLOW = NamedTextColor.YELLOW;

    /**
     * Represents the color white.
     */
    public static final NamedTextColor WHITE = NamedTextColor.WHITE;
}
