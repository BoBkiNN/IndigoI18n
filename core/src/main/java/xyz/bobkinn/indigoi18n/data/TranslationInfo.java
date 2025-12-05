package xyz.bobkinn.indigoi18n.data;

import org.jetbrains.annotations.NotNull;
import xyz.bobkinn.indigoi18n.source.TranslationSource;

public record TranslationInfo(TranslationSource source,
                              String language,
                              String key) {

    @Override
    public @NotNull String toString() {
        var loc = source.getLocation();
        var s = loc != null ? loc.toString() : "unknown";
        return "%s@%s@%s".formatted(key, language, s);
    }
}
