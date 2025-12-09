package xyz.bobkinn.indigoi18n.data;

import lombok.Setter;
import org.jetbrains.annotations.Nullable;
import xyz.bobkinn.indigoi18n.template.TemplateErrorHandler;
import xyz.bobkinn.indigoi18n.template.TemplateParseException;
import xyz.bobkinn.indigoi18n.template.TemplateParser;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TemplateCache {
    /**
     * Map of text to its parsed entry.
     */
    private final Map<String, ParsedEntry> templateCache = new ConcurrentHashMap<>(512);

    @Setter
    private TemplateErrorHandler templateErrorHandler = new TemplateErrorHandler.JULTemplateErrorHandler();

    public ParsedEntry createCache(String text, TranslationInfo info) {
        ParsedEntry entry;
        try {
            entry = TemplateParser.parse(text);
        } catch (TemplateParseException e) {
            if (templateErrorHandler != null) {
                templateErrorHandler.handleParseException(e, text, info);
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
    public @Nullable ParsedEntry getOrCreate(String text, TranslationInfo info) {
        var v = get(text);
        if (v != null) return v;
        return createCache(text, info);
    }
}
