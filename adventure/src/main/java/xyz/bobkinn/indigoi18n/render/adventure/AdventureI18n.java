package xyz.bobkinn.indigoi18n.render.adventure;

import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import xyz.bobkinn.indigoi18n.StringI18n;
import xyz.bobkinn.indigoi18n.render.adventure.format.ComponentRenderer;
import xyz.bobkinn.indigoi18n.render.adventure.format.LegacyComponentRenderer;
import xyz.bobkinn.indigoi18n.render.adventure.format.MiniMessageComponentRenderer;
import xyz.bobkinn.indigoi18n.render.adventure.mixin.LegacyAdventureI18nMixin;
import xyz.bobkinn.indigoi18n.render.adventure.mixin.MiniMessageAdventureI18nMixin;
import xyz.bobkinn.indigoi18n.template.arg.ArgumentConverter;

import java.util.function.Consumer;

/**
 * Default adventure i18n instance that adds {@link LegacyAdventureI18nMixin}
 * and {@link MiniMessageAdventureI18nMixin}<br>
 * Its legacy i18n format converts string arguments using specified legacy component serializer and contains
 * default template formatters.<br>
 * Its MiniMessage i18n format also uses default template formatter.<br>
 * Also it supports {@link AdventureRenderers#PLAIN} format using
 * {@link ComponentRenderer.PlainComponentRenderer}
 */
@SuppressWarnings("unused")
@RequiredArgsConstructor
public class AdventureI18n extends StringI18n implements LegacyAdventureI18nMixin, MiniMessageAdventureI18nMixin {
    private final LegacyComponentSerializer legacyComponentSerializer;
    private final MiniMessage miniMessage;

    public static final LegacyComponentSerializer DEFAULT_LEGACY_SERIALIZER = LegacyComponentSerializer.builder()
            .extractUrls().hexColors().character('&').build();

    public static final MiniMessage DEFAULT_MM_SERIALIZER = MiniMessage.miniMessage();

    public AdventureI18n() {
        this(DEFAULT_LEGACY_SERIALIZER, DEFAULT_MM_SERIALIZER);
    }

    @Override
    protected void addDefaultRenderers() {
        super.addDefaultRenderers(); // add string renderer
        // add plain renderer, used as fallback when inlining
        addRenderer(AdventureRenderers.PLAIN, ComponentRenderer.PlainComponentRenderer::new);
        addRenderer(AdventureRenderers.LEGACY,
                c -> new LegacyComponentRenderer(c, true, legacyComponentSerializer));
        addRenderer(AdventureRenderers.MINI_MESSAGE, c -> new MiniMessageComponentRenderer(c, miniMessage));
    }

    /**
     * Adds converter to template formatter of every {@link ComponentRenderer format} in this I18n instance
     */
    public <T> void putConverter(Class<T> cls, ArgumentConverter<T, Component> conv) {
        visitTemplateFormatters(f -> f.putConverter(cls, conv));
    }

    /**
     * Visit all {@link ComponentRenderer} in this instance and consume their {@link ComponentTemplateFormatter}
     * @param consumer visitor
     */
    public void visitTemplateFormatters(Consumer<ComponentTemplateFormatter> consumer) {
        visitFormats(f -> consumer.accept(f.getTemplateFormatter()));
    }

    /**
     * Invokes consumer with each format that is {@link ComponentRenderer}
     */
    public void visitFormats(Consumer<ComponentRenderer> formatConsumer) {
        for (var f : getRenderers().values()) {
            if (f instanceof ComponentRenderer c) formatConsumer.accept(c);
        }
    }
}
