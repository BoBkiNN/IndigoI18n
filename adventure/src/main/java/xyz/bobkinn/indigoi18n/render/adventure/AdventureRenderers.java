package xyz.bobkinn.indigoi18n.render.adventure;

import net.kyori.adventure.text.Component;
import xyz.bobkinn.indigoi18n.render.RenderType;
import xyz.bobkinn.indigoi18n.render.adventure.format.LegacyComponentRenderer;
import xyz.bobkinn.indigoi18n.render.adventure.format.MiniMessageComponentRenderer;

/**
 * Constants defining common render flavors of {@link Component} output.
 */
public class AdventureRenderers {

    /**
     * Simple {@link Component#text(String)} renderer
     */
    public static final RenderType<Component> PLAIN = new RenderType<>(Component.class, "plain");

    /**
     * Renderer that uses {@link net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer}
     * @see LegacyComponentRenderer
     */
    public static final RenderType<Component> LEGACY = new RenderType<>(Component.class, "legacy");

    /**
     * Renderer that uses {@link net.kyori.adventure.text.minimessage.MiniMessage}
     * @see MiniMessageComponentRenderer
     */
    public static final RenderType<Component> MINI_MESSAGE = new RenderType<>(Component.class, "minimessage");
}
