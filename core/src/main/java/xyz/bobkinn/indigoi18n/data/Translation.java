package xyz.bobkinn.indigoi18n.data;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import xyz.bobkinn.indigoi18n.context.Context;

public abstract class Translation {
    // TODO context overrides here

    public abstract String get(Context ctx);

    public abstract void createCache(TemplateCache cache, TranslationInfo info);

    public abstract void resetCache(TemplateCache cache);

    @Contract("_ -> new")
    public static @NotNull Translation create(String value) {
        return new DefaultTranslation(value);
    }
}
