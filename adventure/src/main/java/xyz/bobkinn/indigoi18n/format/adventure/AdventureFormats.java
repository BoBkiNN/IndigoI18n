package xyz.bobkinn.indigoi18n.format.adventure;

import net.kyori.adventure.text.Component;
import xyz.bobkinn.indigoi18n.format.FormatType;

/**
 * Constants defining common format flavors of {@link Component} output.
 */
public class AdventureFormats {

    public static final FormatType<Component> PLAIN = new FormatType<>(Component.class, "plain");

    public static final FormatType<Component> LEGACY = new FormatType<>(Component.class, "legacy");

    public static final FormatType<Component> MINI_MESSAGE = new FormatType<>(Component.class, "minimessage");
}
