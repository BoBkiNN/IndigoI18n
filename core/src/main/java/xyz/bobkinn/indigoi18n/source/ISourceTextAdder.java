package xyz.bobkinn.indigoi18n.source;

import xyz.bobkinn.indigoi18n.data.Translation;

/**
 * Used by sources to put texts into {@link xyz.bobkinn.indigoi18n.Translations}.
 * Its primary goal to track what have been added
 */
public interface ISourceTextAdder {
    /**
     * This method is called by sources when they are loading translations
     */
    void add(String key, String language, Translation text);
}
