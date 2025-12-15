package xyz.bobkinn.indigoi18n.resolver;

import xyz.bobkinn.indigoi18n.Translations;
import xyz.bobkinn.indigoi18n.context.Context;
import xyz.bobkinn.indigoi18n.data.Translation;

/**
 * Returns key if exact translation for exact language not found inside {@link Translations} texts map
 */
public class DefaultTranslationResolver implements TranslationResolver {

    @Override
    public String get(Context ctx, Translations texts, String key, String lang) {
        return texts.getOr(key, lang, Translation.create(key)).get(ctx, lang);
    }

    @Override
    public String getOrNull(Context ctx, Translations texts, String key, String lang) {
        return texts.getOr(key, lang, null).get(ctx, lang);
    }

    @Override
    public String getOrKey(Context ctx, Translations texts, String key, String lang) {
        return texts.getOr(key, lang, Translation.create(key)).get(ctx, lang);
    }
}
