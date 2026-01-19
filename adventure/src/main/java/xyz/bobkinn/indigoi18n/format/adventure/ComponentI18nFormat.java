package xyz.bobkinn.indigoi18n.format.adventure;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;
import xyz.bobkinn.indigoi18n.context.Context;
import xyz.bobkinn.indigoi18n.data.TemplateCache;
import xyz.bobkinn.indigoi18n.format.I18nFormat;
import xyz.bobkinn.indigoi18n.template.TemplateParseOptions;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class ComponentI18nFormat extends I18nFormat<Component> {

    private final ComponentTemplateFormatter templateFormatter;

    public abstract Component deserializeInput(String text);

    public ComponentI18nFormat(TemplateCache cache, ComponentTemplateFormatter templateFormatter) {
        super(cache);
        this.templateFormatter = templateFormatter;
    }

    @Override
    public Component produce(String text) {
        return deserializeInput(text);
    }

    /**
     * Called when {@link TemplateCache#getOrCreate(Context, String, TemplateParseOptions)} returned null.<br>
     * Default implementation returns translation key text.
     * @param input text component that holds string failing to be parsed
     * @see TemplateCache#getOrCreate(Context, String, TemplateParseOptions)
     */
    @SuppressWarnings("unused")
    protected Component onParsingFailed(@NotNull Context ctx, TextComponent input) {
        return Component.text(ctx.key());
    }

    public Component processText(@NotNull Context ctx, @NotNull TextComponent comp, List<Object> args){
        var full = comp.content();
        var parseOptions = ctx.getOptional(SharedSeqArgContext.class)
                .map(SharedSeqArgContext::getSeqIdx)
                .map(TemplateParseOptions::new)
                .orElseGet(TemplateParseOptions::new);
        var parsed = cache.getOrCreate(ctx, full, parseOptions);
        if (parsed == null) {
            return onParsingFailed(ctx, comp);
        }
        // format parts into component
        var res = templateFormatter.format(ctx, parsed, args);
        // process original children too
        List<Component> extra = new ArrayList<>(comp.children().size());
        for (var c : comp.children()) {
            if (c instanceof TextComponent text){
                extra.add(processText(ctx, text, args));
            } else extra.add(c);
        }
        // apply root style and add children
        return res.mergeStyle(comp).append(extra);
    }

    private void resetSharedSeqIdx(Context ctx) {
        ctx.remove(SharedSeqArgContext.class);
    }

    @Override
    public Component replaceArguments(Context ctx, Component input, List<Object> args) {
        if (input instanceof TextComponent text) {
            var ret = processText(ctx, text, args);
            resetSharedSeqIdx(ctx);
            return ret;
        } else {
            List<Component> extra = new ArrayList<>();
            for (var c : input.children()) {
                if (c instanceof TextComponent text){
                    extra.add(processText(ctx, text, args));
                } else extra.add(c);
            }
            resetSharedSeqIdx(ctx);
            return input.children(extra);
        }
    }

    /**
     * Plain component I18n format. Instead of actually deserializing input
     * it just wraps text into {@link TextComponent}.<br> Used as fallback in some places.
     * @see AdventureFormats#PLAIN
     */
    public static class PlainComponentI18nFormat extends ComponentI18nFormat {

        public PlainComponentI18nFormat(TemplateCache cache, ComponentTemplateFormatter templateFormatter) {
            super(cache, templateFormatter);
        }

        public PlainComponentI18nFormat(TemplateCache cache) {
            this(cache, ComponentTemplateFormatter.defaultString(null));
        }

        @Override
        public Component deserializeInput(String text) {
            return Component.text(text);
        }
    }
}
