package xyz.bobkinn.indigoi18n.source;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import xyz.bobkinn.indigoi18n.data.Translation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
public class SourceTextAdder implements ISourceTextAdder {
    private final ISourceTextAdder addFunction;
    /**
     * Map of key & language to text
     */
    @Getter
    private final Map<Map.Entry<String, String>, Translation> added = new HashMap<>(128);
    /**
     * Map of language to keys
     */
    @Getter
    private final Map<String, Set<String>> addedKeys = new HashMap<>();

    @Override
    public void add(String key, String language, Translation text) {
        added.put(Map.entry(key, language), text);
        addedKeys.computeIfAbsent(language, s -> new HashSet<>()).add(key);
        addFunction.add(key, language, text);
    }
}
