package xyz.bobkinn.indigoi18n.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.bobkinn.indigoi18n.context.Context;

/**
 * Translation is a holder for one or multiple texts.
 * It represents logical text input and unit of text map.
 */
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

    /**
     * Used to correctly create cache for translation data.<br>
     * For example, plural translation can create cache for all of its variants
     * @param cache cache store
     * @param info info about this translation
     */
    public abstract void createCache(TemplateCache cache, TranslationInfo info);

    /**
     * Used to correctly reset cache for translation data.<br>
     * For example, plural translation can reset cache for all of its variants
     * @param cache cache store
     */
    public abstract void resetCache(TemplateCache cache);

    @Contract("_ -> new")
    public static @NotNull Translation create(String value) {
        return new BasicTranslation(value);
    }
}
