package xyz.bobkinn.indigoi18n;

import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.bobkinn.indigoi18n.data.TranslationInfo;
import xyz.bobkinn.indigoi18n.source.SourceTextAdder;
import xyz.bobkinn.indigoi18n.source.TranslationSource;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Translations {
    /**
     * Map of key to map of language to text
     */
    private final Map<String, Map<String, String>> texts = new ConcurrentHashMap<>(512);
    /**
     * Map of source to map of language to keys
     */
    private final Map<TranslationSource, Map<String, Set<String>>> keysBySource = new ConcurrentHashMap<>();

    @Getter
    private final TemplateCache cache = new TemplateCache();

    public List<TranslationSource> sources() {
        return List.copyOf(keysBySource.keySet());
    }

    public void load(@NotNull TranslationSource source) {
        var adder = new SourceTextAdder(this::put);
        source.load(adder);
        var added = adder.getAdded();
        keysBySource.put(source, adder.getAddedKeys());
        for (var e : added.entrySet()) {
            // pair of key and language
            var pair = e.getKey();
            cache.createCache(e.getValue(), new TranslationInfo(source, pair.getValue(), pair.getKey()));
        }
    }

    public void unload(TranslationSource source) {
        var texts = loadedTexts(source);
        if (texts == null) return;
        for (var e : texts.entrySet()) {
            var lang = e.getKey();
            var keys = e.getValue();
            for (var key : keys) {
                var text = remove(lang, key);
                if (text != null) cache.resetCache(text);
            }
        }
    }

    // sources info

    /**
     * @return null if this source is not loaded
     */
    public @Nullable Map<String, Set<String>> loadedTexts(TranslationSource source) {
        return keysBySource.get(source);
    }

    public @NotNull Map<TranslationSource, Set<String>> loadedKeys() {
        var ret = new HashMap<TranslationSource, Set<String>>();
        for (var e : keysBySource.entrySet()) {
            var s = e.getKey();
            var v = e.getValue().values();
            var set = v.stream().flatMap(Collection::stream).collect(Collectors.toSet());
            if (!set.isEmpty()) ret.put(s, set);
        }
        return ret;
    }

    /**
     * Finds first source that has this language and key and creates TranslationInfo
     * @return null if no sources contains text with passed key in passed language
     */
    public @Nullable TranslationInfo infoFor(String key, String lang) {
        for (var e : keysBySource.entrySet()) {
            var keys = e.getValue().get(lang);
            if (keys == null) continue;
            if (keys.contains(key)) {
                return new TranslationInfo(e.getKey(), lang, key);
            }
        }
        return null;
    }

    @SuppressWarnings("unused")
    public Set<TranslationSource> sourcesWith(String key, String lang) {
        var ret = new HashSet<TranslationSource>();
        for (var e : keysBySource.entrySet()) {
            var keys = e.getValue().get(lang);
            if (keys == null) continue;
            if (keys.contains(key)) {
                ret.add(e.getKey());
            }
        }
        return ret;
    }

    @SuppressWarnings("unused")
    public @NotNull Set<TranslationSource> sourcesWith(String key) {
        var ret = new HashSet<TranslationSource>();
        for (var e : loadedKeys().entrySet()) {
            if (e.getValue().contains(key)) ret.add(e.getKey());
        }
        return ret;
    }

    // end sources info

    public void put(String key, String lang, String text) {
        texts.computeIfAbsent(key, (s) -> new ConcurrentHashMap<>())
                .put(lang, text);
    }

    /**
     * @see xyz.bobkinn.indigoi18n.resolver.TranslationResolver
     * @param key translation key
     * @param lang language id
     * @param or value to return if text with that id not found
     * @return text with this key and language or "or" argument
     */
    @Contract("_,_,!null -> !null")
    public String getOr(String key, String lang, String or) {
        var lm = texts.get(key);
        if (lm == null) return or;
        return lm.get(lang);
    }

    public String remove(String lang, String key) {
        var m = texts.get(key);
        if (m == null) return null;
        return m.remove(lang);
    }

}
