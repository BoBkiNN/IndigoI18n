package xyz.bobkinn.indigoi18n.resolver;

import xyz.bobkinn.indigoi18n.IndigoI18n;
import xyz.bobkinn.indigoi18n.Translations;
import xyz.bobkinn.indigoi18n.context.Context;

/**
 * Translation resolver is used to perform lookup in texts map.
 * It is used by {@link IndigoI18n}.<br>
 * Its main purpose is to allow changing how unknown translations are handled.
 */
public interface TranslationResolver {

    /**
     * Lookups text in passed language by passed key
     * @return text or any other value (including null) if text not found
     */
    String get(Context ctx, Translations texts, String key, String lang);

    /**
     * @return string or null if text not found
     * @see #get(Context, Translations, String, String)
     */
    String getOrNull(Context ctx, Translations texts, String key, String lang);

    /**
     * @return string or passed key if text with this key not found
     */
    @SuppressWarnings("unused")
    default String getOrKey(Context ctx, Translations texts, String key, String lang) {
        var v = getOrNull(ctx, texts, key, lang);
        if (v == null) return key;
        return null;
    }
}
