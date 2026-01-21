package xyz.bobkinn.indigoi18n.format.adventure.format;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;
import xyz.bobkinn.indigoi18n.data.TemplateCache;
import xyz.bobkinn.indigoi18n.format.adventure.ComponentTemplateFormatter;
import xyz.bobkinn.indigoi18n.template.format.StringTemplateFormatter;

/**
 * Default format that parses input text into Component using {@link net.kyori.adventure.text.minimessage.MiniMessage}
 */
@Getter
public class MiniMessageComponentRenderer extends ComponentRenderer {
    private final MiniMessage serializer;

    public MiniMessageComponentRenderer(TemplateCache cache,
                                        ComponentTemplateFormatter templateFormatter, MiniMessage serializer) {
        super(cache, templateFormatter);
        this.serializer = serializer;
    }

    /**
     * Creates new format with {@link ComponentTemplateFormatter} that uses {@link StringTemplateFormatter}.<br>
     * @param serializer serializer used to convert text into Component and to convert String arguments into Component
     */
    public MiniMessageComponentRenderer(TemplateCache cache, @NotNull MiniMessage serializer) {
        this(cache, new ComponentTemplateFormatter(new StringTemplateFormatter(), null), serializer);
    }

    @Override
    public Component deserializeInput(String text) {
        return serializer.deserialize(text);
    }
}
