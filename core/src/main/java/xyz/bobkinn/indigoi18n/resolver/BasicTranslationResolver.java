package xyz.bobkinn.indigoi18n.resolver;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.bobkinn.indigoi18n.Translations;
import xyz.bobkinn.indigoi18n.context.Context;
import xyz.bobkinn.indigoi18n.data.Translation;

/**
 * Returns key if exact translation for exact language not found inside {@link Translations} texts map
 */
public class BasicTranslationResolver implements TranslationResolver {

    /**
     * Called when no translation found in text map.
     * Default implementation return text containing key itself
     * @param texts text map used to lookup text
     * @param key translation key
     * @param lang requested language
     * @return non-null translation
     */
    @SuppressWarnings("unused")
    protected @NotNull Translation getMissingText(Translations texts, String key, String lang) {
        return Translation.create(key);
    }

    @Override
    public Translation get(Context ctx, Translations texts, String key, String lang) {
        var v = getOrNull(ctx, texts, key, lang);
        if (v != null) return v;
        return getMissingText(texts, key, lang);
    }

    @Override
    public @Nullable Translation getOrNull(Context ctx, Translations texts, String key, String lang) {
        return texts.getOr(key, lang, null);
    }

    @Override
    public Translation getOrKey(Context ctx, Translations texts, String key, String lang) {
        var v = getOrNull(ctx, texts, key, lang);
        if (v != null) return v;
        return Translation.create(key);
    }
}
