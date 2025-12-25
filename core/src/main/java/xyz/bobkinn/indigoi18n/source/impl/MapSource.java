package xyz.bobkinn.indigoi18n.source.impl;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import xyz.bobkinn.indigoi18n.data.Translation;
import xyz.bobkinn.indigoi18n.source.ISourceTextAdder;
import xyz.bobkinn.indigoi18n.source.TranslationSource;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple source that just stores translations inside map.
 */
@SuppressWarnings("unused")
@RequiredArgsConstructor
public class MapSource implements TranslationSource {
    private final URI location;
    /**
     * Map of key to map of lang to translation
     */
    private final Map<String, Map<String, Translation>> texts;

    public MapSource(URI location) {
        this(location, new HashMap<>());
    }

    public MapSource() {
        this(null);
    }

    public void put(String key, String lang, Translation translation) {
        texts.computeIfAbsent(key, s -> new HashMap<>())
                .put(lang, translation);
    }

    public void putBasic(String key, String lang, String text) {
        put(key, lang, Translation.create(text));
    }

    @Override
    public void load(ISourceTextAdder to) {
        for (var e : texts.entrySet()) {
            var key = e.getKey();
            for (var tr : e.getValue().entrySet()) {
                to.add(key, tr.getKey(), tr.getValue());
            }
        }
    }

    @Override
    public @Nullable URI getLocation() {
        return location;
    }
}
