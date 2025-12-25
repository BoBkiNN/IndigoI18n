package xyz.bobkinn.indigoi18n.resolver;

import org.jetbrains.annotations.Nullable;
import xyz.bobkinn.indigoi18n.Translations;
import xyz.bobkinn.indigoi18n.context.Context;
import xyz.bobkinn.indigoi18n.data.Translation;

/**
 * Dynamic resolver can be used to query translations bypassing {@link Translations}.<br>
 * Its default {@link #get(Context, Translations, String, String)} method returns key if abstract method return null
 */
@SuppressWarnings("unused")
public interface DynamicResolver extends TranslationResolver {

    /**
     * @return null if translation not found
     */
    @Nullable Translation get(Context ctx, String key, String lang);

    @Override
    default Translation get(Context ctx, Translations texts, String key, String lang) {
        var t = get(ctx, key, lang);
        if (t != null) return t;
        return Translation.create(key);
    }

    @Override
    default @Nullable Translation getOrNull(Context ctx, Translations texts, String key, String lang) {
        return get(ctx, key, lang);
    }
}
