package xyz.bobkinn.indigoi18n;

import lombok.Setter;
import xyz.bobkinn.indigoi18n.data.ParsedEntry;
import xyz.bobkinn.indigoi18n.data.TranslationInfo;
import xyz.bobkinn.indigoi18n.template.TemplateParseException;
import xyz.bobkinn.indigoi18n.template.TemplateProcessor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TemplateCache {
    /**
     * Map of text to its parsed entry.
     */
    private final Map<String, ParsedEntry> templateCache = new ConcurrentHashMap<>(512);

    @Setter
    private TemplateErrorHandler templateErrorHandler = new TemplateErrorHandler.JULTemplateErrorHandler();

    public void createCache(String text, TranslationInfo info) {
        ParsedEntry entry;
        try {
            entry = TemplateProcessor.parse(text);
        } catch (TemplateParseException e) {
            if (templateErrorHandler != null) {
                templateErrorHandler.handleParseException(e, text, info);
                return;
            } else throw e;
        }
        templateCache.put(text, entry);
    }

    public void resetCache(String text) {
        templateCache.remove(text);
    }
}
