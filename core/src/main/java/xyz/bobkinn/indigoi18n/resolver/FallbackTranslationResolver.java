package xyz.bobkinn.indigoi18n.resolver;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.bobkinn.indigoi18n.Translations;
import xyz.bobkinn.indigoi18n.context.Context;
import xyz.bobkinn.indigoi18n.data.Translation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This translation resolver will perform multiple fallback steps to resolve text:<br>
 * <ol>
 *     <li>Lookup by exact requested language</li>
 *     <li>Generalize language and lookup by it, i.e en_us -> en.</li>
 *     <li>Lookup by default language, for example "en"</li>
 *     <li>Return text denoting missing translation, usually requested key
 *     or null when {@link #getOrNull(Context, Translations, String, String)} is used</li>
 * </ol>
 */
@AllArgsConstructor
@Setter
@Getter
public class FallbackTranslationResolver extends BasicTranslationResolver {
    private String defaultLanguage;

    public static List<String> generalizeLanguage(String lang) {
        String[] parts = lang.split("_");

        // usually language, language_region, language_region_variant
        List<String> result = new ArrayList<>(3);

        for (int i = parts.length; i > 0; i--) {
            result.add(String.join("_", Arrays.copyOfRange(parts, 0, i)));
        }

        return result;
    }


    protected Translation getClosest(Translations texts, String key, String lang) {
        var chain = generalizeLanguage(lang);
        for (var e : chain) {
            var t = texts.getOrNull(key, e);
            if (t != null) return t;
        }
        return null;
    }

    /**
     * Never returns key itself. Only real texts.<br>
     * Default implementation uses {@link #defaultLanguage} ignoring requested.<br>
     * This method is used when no closest language found. Last resort before returning absence of text denoted by key
     */
    @SuppressWarnings("unused")
    protected Translation getFallback(Translations texts, String key, String lang) {
        return texts.getOrNull(key, defaultLanguage);
    }

    /**
     * Returns missing text translation when no actual data can be displayed.
     * Default implementation returns key itself
     * @param texts text map, can be used to display no translation message or similar
     * @param key key of requested translation
     * @param lang requested language
     */
    @SuppressWarnings("unused")
    protected @NotNull Translation getMissingText(Translations texts, String key, String lang) {
        return Translation.create(key);
    }

    @Override
    public Translation get(Context ctx, Translations texts, String key, String lang) {
        var cl = getClosest(texts, key, lang);
        if (cl != null) return cl;
        // if no closest fallback found, return last resort
        var fb = getFallback(texts, key, lang);
        if (fb != null) return fb;
        return getMissingText(texts, key, lang);
    }

    @Override
    public @Nullable Translation getOrNull(Context ctx, Translations texts, String key, String lang) {
        var cl = getClosest(texts, key, lang);
        if (cl != null) return cl; // return closest
        return getFallback(texts, key, lang);
    }
}
