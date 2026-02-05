package xyz.bobkinn.indigoi18n.resolver;

import org.jetbrains.annotations.Nullable;
import xyz.bobkinn.indigoi18n.Translations;
import xyz.bobkinn.indigoi18n.context.Context;
import xyz.bobkinn.indigoi18n.data.Translation;

/**
 * Returns key if exact translation for exact language not found inside {@link Translations} texts map
 */
public class BasicTranslationResolver implements TranslationResolver {

    @Override
    public Translation get(Context ctx, Translations texts, String key, String lang) {
        var v = getOrNull(ctx, texts, key, lang);
        if (v != null) return v;
        return Translation.create(key);
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
