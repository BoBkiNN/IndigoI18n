package xyz.bobkinn.indigoi18n.source;

import xyz.bobkinn.indigoi18n.data.Translation;

public interface ISourceTextAdder {
    void add(String key, String language, Translation text);
}
