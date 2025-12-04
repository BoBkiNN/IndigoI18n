package xyz.bobkinn.indigoi18n.resolver;

import xyz.bobkinn.indigoi18n.Translations;

/**
 * Returns key if exact translation for exact language not found
 */
public class BasicTranslationResolver implements TranslationResolver {

    @Override
    public String get(Translations texts, String key, String lang) {
        return texts.getOr(key, lang, key);
    }

    @Override
    public String getOrNull(Translations texts, String key, String lang) {
        return texts.getOr(key, lang, null);
    }

    @Override
    public String getOrKey(Translations texts, String key, String lang) {
        return texts.getOr(key, lang, key);
    }
}
