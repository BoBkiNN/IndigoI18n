package xyz.bobkinn.indigoi18n.render.adventure.format;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;
import xyz.bobkinn.indigoi18n.data.TemplateCache;
import xyz.bobkinn.indigoi18n.render.adventure.ComponentTemplateFormatter;
import xyz.bobkinn.indigoi18n.template.format.StringTemplateFormatter;

/**
 * Default format that parses input text into Component using {@link LegacyComponentSerializer}
 */
@Getter
public class LegacyComponentRenderer extends ComponentRenderer {
    private final LegacyComponentSerializer serializer;

    public LegacyComponentRenderer(TemplateCache cache,
                                   ComponentTemplateFormatter templateFormatter, LegacyComponentSerializer serializer) {
        super(cache, templateFormatter);
        this.serializer = serializer;
    }

    /**
     * Creates new format with {@link ComponentTemplateFormatter} that uses {@link StringTemplateFormatter}.<br>
     * If {@code convertStringArgument} is true, {@link ComponentTemplateFormatter} will use {@code serializer} to
     * format String arguments using it
     * @param serializer serializer used to convert text into Component and to convert String arguments into Component
     */
    public LegacyComponentRenderer(TemplateCache cache, @NotNull LegacyComponentSerializer serializer) {
        this(cache, ComponentTemplateFormatter.defaultString(), serializer);
    }

    @Override
    public Component deserializeInput(String text) {
        return serializer.deserialize(text);
    }
}
