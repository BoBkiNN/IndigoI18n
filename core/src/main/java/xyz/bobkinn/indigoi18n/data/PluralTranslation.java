package xyz.bobkinn.indigoi18n.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.xyzsd.plurals.PluralCategory;
import net.xyzsd.plurals.PluralRule;
import net.xyzsd.plurals.PluralRuleType;
import org.jetbrains.annotations.NotNull;
import xyz.bobkinn.indigoi18n.context.Context;
import xyz.bobkinn.indigoi18n.context.impl.CountContext;

import java.util.Map;

/**
 * Plural translations are used for text to have different variants for plural category
 * @see CountContext
 * @see PluralResolutionException
 * @see PluralCategory
 */
@RequiredArgsConstructor
@Getter
public class PluralTranslation extends Translation {
    private final Map<PluralCategory, String> plurals;

    public PluralTranslation(Map<PluralCategory, String> plurals, Context contextOverride) {
        super(contextOverride);
        this.plurals = plurals;
    }

    public String get(PluralCategory category) {
        var v = plurals.get(category);
        if (v != null) return v;
        var ov = plurals.get(PluralCategory.OTHER);
        if (ov != null) return ov;
        return plurals.entrySet().stream().findFirst()
                .orElseThrow(() -> new PluralResolutionException("No %s, OTHER, or any plural value found"
                        .formatted(category.name()))
                ).getValue();
    }

    /**
     * Thrown when plural cannot be selected
     */
    public static class PluralResolutionException extends IllegalStateException {
        public PluralResolutionException(String msg) {
            super(msg);
        }
    }

    /**
     * If count context not found, {@link PluralCategory#OTHER} is used
     */
    @Override
    public @NotNull String resolve(Context ctx) {
        var count = ctx.resolveOptional(CountContext.class)
                .map(CountContext::getCount).orElse(null);
        if (count == null) {
            return get(PluralCategory.OTHER);
        }
        var lang = ctx.language();
        var lr = ctx.getI18n().getLocaleResolver();
        var locale = lr.getLocale(lang);
        if (locale == null) throw new PluralResolutionException("No locale resolved from language "+lang);
        // trivial
        var rule = PluralRule.create(locale, PluralRuleType.CARDINAL).orElse(null);
        if (rule == null) throw new PluralResolutionException("No rule found for locale "+locale);
        var cat = rule.select(count);
        return get(cat);
    }

    @Override
    public void createCache(TemplateCache cache, TranslationInfo info) {
        for (var v : plurals.values()) {
            cache.createCache(v, info);
        }
    }

    @Override
    public void resetCache(TemplateCache cache) {
        for (var v : plurals.values()) {
            cache.resetCache(v);
        }
    }
}
