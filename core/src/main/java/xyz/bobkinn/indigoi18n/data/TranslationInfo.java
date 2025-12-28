package xyz.bobkinn.indigoi18n.data;

import org.jetbrains.annotations.NotNull;
import xyz.bobkinn.indigoi18n.context.Context;
import xyz.bobkinn.indigoi18n.context.impl.LangKeyContext;
import xyz.bobkinn.indigoi18n.context.impl.SourceContext;
import xyz.bobkinn.indigoi18n.source.TranslationSource;

public record TranslationInfo(TranslationSource source,
                              String language,
                              String key) {

    public Context asContext() {
        return new Context()
                .with(new SourceContext(source()))
                .with(new LangKeyContext(language(), key(), null));
    }

    @Override
    public @NotNull String toString() {
        var loc = source.getLocation();
        var s = loc != null ? loc.toString() : "unknown";
        return "%s@%s@%s".formatted(key, language, s);
    }
}
