package xyz.bobkinn.indigoi18n.resolver;

import xyz.bobkinn.indigoi18n.Translations;
import xyz.bobkinn.indigoi18n.context.Context;
import xyz.bobkinn.indigoi18n.data.Translation;

/**
 * Returns key if exact translation for exact language not found inside {@link Translations} texts map
 */
public class DefaultTranslationResolver implements TranslationResolver {

    @Override
    public Translation get(Context ctx, Translations texts, String key, String lang) {
        return texts.getOr(key, lang, Translation.create(key));
    }

    @Override
    public Translation getOrNull(Context ctx, Translations texts, String key, String lang) {
        return texts.getOr(key, lang, null);
    }

    @Override
    public Translation getOrKey(Context ctx, Translations texts, String key, String lang) {
        return texts.getOr(key, lang, Translation.create(key));
    }
}
