package xyz.bobkinn.indigoi18n.source;

import org.jetbrains.annotations.Nullable;
import xyz.bobkinn.indigoi18n.IndigoI18n;

import java.net.URI;

public interface TranslationSource {

    /**
     * Called by {@link IndigoI18n#load(TranslationSource)}
     * to add translations from source into translations table
     * @param to text adder that is used to add translations
     */
    void load(ISourceTextAdder to);

    /**
     * @return URI displayed in debug to distinguish between different sources
     */
    @Nullable URI getLocation();
}
