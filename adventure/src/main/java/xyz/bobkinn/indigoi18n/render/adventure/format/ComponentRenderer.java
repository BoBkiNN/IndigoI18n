package xyz.bobkinn.indigoi18n.render.adventure.format;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;
import xyz.bobkinn.indigoi18n.context.Context;
import xyz.bobkinn.indigoi18n.data.TemplateCache;
import xyz.bobkinn.indigoi18n.render.Renderer;
import xyz.bobkinn.indigoi18n.render.adventure.AdventureRenderers;
import xyz.bobkinn.indigoi18n.render.adventure.ComponentTemplateFormatter;
import xyz.bobkinn.indigoi18n.render.adventure.SharedSeqArgContext;
import xyz.bobkinn.indigoi18n.template.TemplateParseOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public abstract class ComponentRenderer extends Renderer<Component> {

    private final ComponentTemplateFormatter templateFormatter;

    private final Map<String, Component> producerCache = new ConcurrentHashMap<>();

    /**
     * Toggles caching and cache usage of {@link #deserializeInput(String)} calls
     */
    @Getter
    @Setter
    private boolean producerCacheEnabled = true;

    /**
     * Clears producer cache for specified text
     * @param text cache key
     */
    @SuppressWarnings("unused")
    public void resetProducerCache(String text) {
        producerCache.remove(text);
    }

    /**
     * Clears all producer cache entries
     */
    @SuppressWarnings("unused")
    public void resetProducerCache() {
        producerCache.clear();
    }

    public abstract Component deserializeInput(String text);

    public ComponentRenderer(TemplateCache cache, ComponentTemplateFormatter templateFormatter) {
        super(cache);
        this.templateFormatter = templateFormatter;
    }

    @Override
    public Component produce(String text) {
        if (producerCacheEnabled) {
            // lookup cache
            var cached = producerCache.get(text);
            if (cached != null) return cached;
        }
        var v = deserializeInput(text);
        if (producerCacheEnabled) producerCache.put(text, v);
        return v;
    }

    /**
     * Called when {@link TemplateCache#getOrCompute(Context, String, TemplateParseOptions)} returned null.<br>
     * Default implementation returns translation key text.
     * @param input text component that holds string failing to be parsed
     * @see TemplateCache#getOrCompute(Context, String, TemplateParseOptions)
     */
    @SuppressWarnings("unused")
    protected Component onParsingFailed(@NotNull Context ctx, TextComponent input) {
        return Component.text(ctx.key());
    }

    /**
     * Method used by default {@link #replaceArguments(Context, Component, List)} implementation
     */
    public Component processText(@NotNull Context ctx, @NotNull TextComponent comp, List<Object> args){
        var full = comp.content();
        var parseOptions = ctx.getOptional(SharedSeqArgContext.class)
                .map(SharedSeqArgContext::getSeqIdx)
                .map(TemplateParseOptions::new)
                .orElseGet(TemplateParseOptions::new);
        var parsed = cache.getOrCompute(ctx, full, parseOptions);
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
        // .append method added in adventure 4.20
        var nc = new ArrayList<>(res.children());
        nc.addAll(extra);
        return res.mergeStyle(comp).children(nc);
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
     * @see AdventureRenderers#PLAIN
     */
    public static class PlainComponentRenderer extends ComponentRenderer {

        public PlainComponentRenderer(TemplateCache cache, ComponentTemplateFormatter templateFormatter) {
            super(cache, templateFormatter);
        }

        @SuppressWarnings("unused")
        public PlainComponentRenderer(TemplateCache cache) {
            this(cache, ComponentTemplateFormatter.defaultString());
        }

        @Override
        public Component deserializeInput(String text) {
            return Component.text(text);
        }
    }
}
