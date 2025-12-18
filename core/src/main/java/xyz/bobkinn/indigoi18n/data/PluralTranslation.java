package xyz.bobkinn.indigoi18n.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.xyzsd.plurals.PluralCategory;
import net.xyzsd.plurals.PluralRule;
import net.xyzsd.plurals.PluralRuleType;
import xyz.bobkinn.indigoi18n.context.Context;
import xyz.bobkinn.indigoi18n.context.impl.CountContext;

import java.util.Map;

@RequiredArgsConstructor
@Getter
public class PluralTranslation extends Translation {
    private final Map<PluralCategory, String> plurals;

    public String get(PluralCategory category) {
        var v = plurals.get(category);
        if (v != null) return v;
        var ov = plurals.get(PluralCategory.OTHER);
        if (ov != null) return ov;
        return plurals.entrySet().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("No %s, OTHER, or any plural value"
                        .formatted(category.name()))
                ).getValue();
    }

    @Override
    public String get(Context ctx, String lang) {
        var count = ctx.resolveOptional(CountContext.class)
                .map(CountContext::getCount).orElse(null);
        if (count == null) {
            return get(PluralCategory.OTHER);
        }
        var lr = ctx.getI18n().getLocaleResolver();
        var locale = lr.getLocale(lang);
        if (locale == null) throw new IllegalStateException("No locale resolved from language "+lang);
        var rule = PluralRule.create(locale, PluralRuleType.CARDINAL).orElse(null);
        if (rule == null) throw new IllegalStateException("No rule found for locale "+locale);
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
