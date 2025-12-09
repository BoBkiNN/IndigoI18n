package xyz.bobkinn.indigoi18n.source;

import org.jetbrains.annotations.Nullable;

import java.net.URI;

public interface TranslationSource {

    /**
     * Called by {@link xyz.bobkinn.indigoi18n.I18n#load(TranslationSource)}
     * to add translations from source into translations table
     * @param to text adder that is used to add translations
     */
    void load(ISourceTextAdder to);

    /**
     * @return URI displayed in debug to distinguish between different sources
     */
    @Nullable URI getLocation();
}
