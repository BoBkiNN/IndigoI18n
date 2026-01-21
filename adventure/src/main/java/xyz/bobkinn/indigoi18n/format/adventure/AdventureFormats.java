package xyz.bobkinn.indigoi18n.format.adventure;

import net.kyori.adventure.text.Component;
import xyz.bobkinn.indigoi18n.format.FormatType;

/**
 * Constants defining common format flavors of {@link Component} output.
 */
public class AdventureFormats {

    /**
     * Simple {@link Component#text(String)} renderer
     */
    public static final FormatType<Component> PLAIN = new FormatType<>(Component.class, "plain");

    /**
     * Renderer that uses {@link net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer}
     * @see xyz.bobkinn.indigoi18n.format.adventure.format.LegacyComponentI18nFormat
     */
    public static final FormatType<Component> LEGACY = new FormatType<>(Component.class, "legacy");

    /**
     * Renderer that uses {@link net.kyori.adventure.text.minimessage.MiniMessage}
     * @see xyz.bobkinn.indigoi18n.format.adventure.format.MiniMessageComponentI18nFormat
     */
    public static final FormatType<Component> MINI_MESSAGE = new FormatType<>(Component.class, "minimessage");
}
