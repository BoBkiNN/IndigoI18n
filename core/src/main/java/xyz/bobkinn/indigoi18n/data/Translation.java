package xyz.bobkinn.indigoi18n.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.bobkinn.indigoi18n.context.Context;

@Getter
@RequiredArgsConstructor
public abstract class Translation {
    /**
     * Context overrides is applied before translation resolving and further formatting
     */
    private final @Nullable Context contextOverride;

    public Translation() {
        this(null);
    }

    /**
     * Used to select text from this translation
     * @param ctx context that can be used to resolve text based on some conditions,
     *            for example {@link xyz.bobkinn.indigoi18n.context.impl.CountContext}
     * @return resolved string
     */
    public abstract @NotNull String resolve(Context ctx);

    public abstract void createCache(TemplateCache cache, TranslationInfo info);

    public abstract void resetCache(TemplateCache cache);

    @Contract("_ -> new")
    public static @NotNull Translation create(String value) {
        return new DefaultTranslation(value);
    }
}
