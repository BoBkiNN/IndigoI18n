package xyz.bobkinn.indigoi18n.source;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
public class SourceTextAdder implements ISourceTextAdder {
    private final ISourceTextAdder addFunction;
    @Getter
    private final Map<String, Set<String>> addedKeys = new HashMap<>();

    @Override
    public void add(String key, String language, String text) {
        addedKeys.computeIfAbsent(language, s -> new HashSet<>()).add(key);
        addFunction.add(key, language, text);
    }
}
