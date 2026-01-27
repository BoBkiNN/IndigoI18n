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

/**
 * @see #getOrCompute(Context, String, TemplateParseOptions) common entrypoint 
 */
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

    /**
     * When disabled, {@link #getOrCompute(Context, String, TemplateParseOptions)} 
     * only calls {@link #parse(Context, String, TemplateParseOptions)}
     */
    @Setter
    @Getter
    private boolean enabled = true;

    @SuppressWarnings("UnusedReturnValue")
    public ParsedEntry computeCache(String text, TranslationInfo info) {
        return computeCache(info.asContext(), text, new TemplateParseOptions());
    }

    /**
     * Parses template text using
     * {@link xyz.bobkinn.indigoi18n.template.TemplateParser#parse(String, TemplateParseOptions)}
     * and handles parsing exception
     * @param ctx context that is passed to error handler in case of exception
     * @return null if {@link TemplateParseException} is thrown and handled
     * @see TemplateErrorHandler
     * @see #setTemplateErrorHandler(TemplateErrorHandler)
     * @see TemplateParseOptions
     */
    public @Nullable ParsedEntry parse(Context ctx, String text, TemplateParseOptions parseOptions) {
        try {
            return templateParser.parse(text, parseOptions);
        } catch (TemplateParseException e) {
            if (templateErrorHandler != null) {
                templateErrorHandler.handleParseException(e, text, ctx);
                return null;
            } else throw e;
        }
    }

    /**
     * Parses template text and caches resulting {@link ParsedEntry}
     * @return parsed entry or null if failed to parse
     * @see #parse(Context, String, TemplateParseOptions)
     */
    public ParsedEntry computeCache(Context ctx, String text, TemplateParseOptions parseOptions) {
        var entry = parse(ctx, text, parseOptions);
        if (entry != null) templateCache.put(text, entry);
        return entry;
    }

    public void resetCache(String text) {
        templateCache.remove(text);
    }


    /**
     * Reset all cache
     * @return number of cleared entries
     */
    @SuppressWarnings("unused")
    public int reset() {
        var c = templateCache.size();
        templateCache.clear();
        return c;
    }

    /**
     * @return cached entry or null if not cached
     */
    public @Nullable ParsedEntry get(String text) {
        return templateCache.get(text);
    }

    /**
     * If caching is disabled, cache lookup and compute is skipped
     * and {@link #parse(Context, String, TemplateParseOptions)} is called directly instead
     * @return null if failed to parse
     */
    public @Nullable ParsedEntry getOrCompute(Context ctx, String text, TemplateParseOptions parseOptions) {
        if (!enabled) return parse(ctx, text, parseOptions);
        var v = get(text);
        if (v != null) return v;
        return computeCache(ctx, text, parseOptions);
    }

    /**
     * @return null if failed to parse
     */
    public @Nullable ParsedEntry getOrCompute(Context ctx, String text) {
        return getOrCompute(ctx, text, new TemplateParseOptions());
    }
}
