package xyz.bobkinn.indigoi18n.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;
import xyz.bobkinn.indigoi18n.context.Context;
import xyz.bobkinn.indigoi18n.template.ITemplateParser;
import xyz.bobkinn.indigoi18n.template.TemplateErrorHandler;
import xyz.bobkinn.indigoi18n.template.TemplateParseException;
import xyz.bobkinn.indigoi18n.template.TemplateParseOptions;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class TemplateCache {
    /**
     * Template parser used to parse translation text
     */
    @Getter
    private final ITemplateParser templateParser;
    /**
     * Map of text to its parsed entry.
     */
    private final Map<String, ParsedEntry> templateCache = new ConcurrentHashMap<>(512);

    @Setter
    private TemplateErrorHandler templateErrorHandler = new TemplateErrorHandler.JULTemplateErrorHandler();

    @SuppressWarnings("UnusedReturnValue")
    public ParsedEntry createCache(String text, TranslationInfo info) {
        return createCache(info.asContext(), text, new TemplateParseOptions());
    }

    public ParsedEntry createCache(Context ctx, String text, TemplateParseOptions parseOptions) {
        ParsedEntry entry;
        try {
            entry = templateParser.parse(text, parseOptions);
        } catch (TemplateParseException e) {
            if (templateErrorHandler != null) {
                templateErrorHandler.handleParseException(e, text, ctx);
                return null;
            } else throw e;
        }
        templateCache.put(text, entry);
        return entry;
    }

    public void resetCache(String text) {
        templateCache.remove(text);
    }

    public @Nullable ParsedEntry get(String text) {
        return templateCache.get(text);
    }

    /**
     * @return null if failed to parse
     */
    public @Nullable ParsedEntry getOrCreate(Context ctx, String text, TemplateParseOptions parseOptions) {
        var v = get(text);
        if (v != null) return v;
        return createCache(ctx, text, parseOptions);
    }

    /**
     * @return null if failed to parse
     */
    public @Nullable ParsedEntry getOrCreate(Context ctx, String text) {
        return getOrCreate(ctx, text, new TemplateParseOptions());
    }
}
