package xyz.bobkinn.indigoi18n.render.impl;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import xyz.bobkinn.indigoi18n.context.Context;
import xyz.bobkinn.indigoi18n.data.TemplateCache;
import xyz.bobkinn.indigoi18n.render.Renderer;
import xyz.bobkinn.indigoi18n.template.TemplateParseOptions;
import xyz.bobkinn.indigoi18n.template.format.StringTemplateFormatter;
import xyz.bobkinn.indigoi18n.template.format.TemplateFormatter;

import java.util.List;

@Getter
public class StringRenderer extends Renderer<String> {
    /**
     * Template formatter that is used to format input strings
     */
    private final TemplateFormatter<String> templateFormatter;

    public StringRenderer(TemplateCache cache) {
        super(cache);
        templateFormatter = new StringTemplateFormatter();
    }

    @Override
    public String produce(String text) {
        return text;
    }

    /**
     * Called when {@link TemplateCache#getOrCompute(Context, String, TemplateParseOptions)} returned null.<br>
     * Default implementation returns translation key text.
     * @param input string failing to be parsed
     * @see TemplateCache#getOrCompute(Context, String, TemplateParseOptions)
     */
    @SuppressWarnings("unused")
    protected String onParsingFailed(@NotNull Context ctx, String input) {
        return ctx.key();
    }

    @Override
    public String replaceArguments(Context ctx, String input, List<Object> args) {
        var parsed = cache.getOrCompute(ctx, input);
        if (parsed == null) {
            return onParsingFailed(ctx, input);
        }
        return templateFormatter.format(ctx, parsed, args);
    }
}
