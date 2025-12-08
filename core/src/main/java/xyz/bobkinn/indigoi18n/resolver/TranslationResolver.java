package xyz.bobkinn.indigoi18n.resolver;

import xyz.bobkinn.indigoi18n.Translations;

public interface TranslationResolver {

    String get(Translations texts, String key, String lang);

    String getOrNull(Translations texts, String key, String lang);

    @SuppressWarnings("unused")
    default String getOrKey(Translations texts, String key, String lang) {
        var v = getOrNull(texts, key, lang);
        if (v == null) return key;
        return null;
    }
}
