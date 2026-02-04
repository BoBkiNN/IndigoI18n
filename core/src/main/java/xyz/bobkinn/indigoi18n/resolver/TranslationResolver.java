package xyz.bobkinn.indigoi18n.resolver;

import org.jetbrains.annotations.Nullable;
import xyz.bobkinn.indigoi18n.IndigoI18n;
import xyz.bobkinn.indigoi18n.Translations;
import xyz.bobkinn.indigoi18n.context.Context;
import xyz.bobkinn.indigoi18n.data.BasicTranslation;
import xyz.bobkinn.indigoi18n.data.Translation;

/**
 * Translation resolver is used to perform lookup in texts map.
 * It is used by {@link IndigoI18n}.<br>
 * Its main purpose is to allow changing how unknown translations are handled.
 */
public interface TranslationResolver {

    /**
     * Lookups translation in passed language by passed key
     * @return translation or any other value (including null) if text not found
     */
    Translation get(Context ctx, Translations texts, String key, String lang);

    /**
     * Used when absence of any result must be null
     * @return null if translation not found or no any real result can be returned
     * @see #get(Context, Translations, String, String)
     */
    @Nullable Translation getOrNull(Context ctx, Translations texts, String key, String lang);

    /**
     * @return translation or passed key inside {@link BasicTranslation}
     * if text with this key not found
     */
    @SuppressWarnings("unused")
    default Translation getOrKey(Context ctx, Translations texts, String key, String lang) {
        var v = getOrNull(ctx, texts, key, lang);
        if (v == null) return Translation.create(key);
        return v;
    }
}
