package xyz.bobkinn.indigoi18n.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.bobkinn.indigoi18n.context.Context;
import xyz.bobkinn.indigoi18n.context.impl.LangKeyContext;
import xyz.bobkinn.indigoi18n.context.impl.SourceContext;
import xyz.bobkinn.indigoi18n.source.TranslationSource;

public record TranslationInfo(@Nullable TranslationSource source,
                              String language,
                              String key) {

    public Context asContext() {
        var ctx =  new Context()
                .with(new LangKeyContext(language(), key(), null));
        if (source != null) {
            ctx.with(new SourceContext(source()));
        }
        return ctx;
    }

    @Override
    public @NotNull String toString() {
        var loc = source != null ? source.getLocation() : null;
        var s = loc != null ? loc.toString() : "unknown";
        return "%s@%s@%s".formatted(key, language, s);
    }
}
