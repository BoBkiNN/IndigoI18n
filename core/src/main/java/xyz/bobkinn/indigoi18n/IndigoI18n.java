package xyz.bobkinn.indigoi18n;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.bobkinn.indigoi18n.context.Context;
import xyz.bobkinn.indigoi18n.data.Translation;
import xyz.bobkinn.indigoi18n.data.TranslationInfo;
import xyz.bobkinn.indigoi18n.format.I18nFormat;
import xyz.bobkinn.indigoi18n.resolver.DefaultTranslationResolver;
import xyz.bobkinn.indigoi18n.resolver.TranslationResolver;
import xyz.bobkinn.indigoi18n.source.TranslationSource;
import xyz.bobkinn.indigoi18n.template.ITemplateParser;
import xyz.bobkinn.indigoi18n.template.TemplateParser;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * I18n with no formats or its access methods.<br>
 * Uses default resolver and do not include any translations
 * @see StringI18n
 */
public class IndigoI18n implements I18nEngine {
    @Getter
    protected final Translations texts;
    /**
     * Resolver that is used to lookup texts to find text by key and language
     */
    @Getter
    private @NotNull TranslationResolver resolver;
    private final Map<Class<?>, I18nFormat<?>> formats;

    @Getter
    private final LocaleResolver localeResolver;

    private final Map<String, Locale> localeCache = new ConcurrentHashMap<>();

    public IndigoI18n(ITemplateParser templateParser, LocaleResolver localeResolver) {
        texts = new Translations(templateParser);
        resolver = new DefaultTranslationResolver();
        formats = new HashMap<>();
        this.localeResolver = localeResolver;
        addDefaultFormats();
    }

    public IndigoI18n() {
        this(TemplateParser.INSTANCE, LocaleResolver.DEFAULT);
    }

    public <T> void addFormat(Class<T> cls, I18nFormat<T> format) {
        formats.put(cls, format);
    }

    protected void addDefaultFormats() {
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> I18nFormat<T> getFormat(Class<T> cls) {
        var f = (I18nFormat<T>) formats.get(cls);
        return Objects.requireNonNull(f, "Unknown format for "+cls.getSimpleName());
    }

    public void load(TranslationSource source) {
        texts.load(source);
    }

    public void unload(TranslationSource source) {
        texts.unload(source);
    }

    @SuppressWarnings("unused")
    public List<TranslationSource> sources() {
        return texts.sources();
    }

    @Override
    public TranslationInfo infoFor(String lang, String key) {
        return texts.infoFor(key, lang);
    }

    @SuppressWarnings("unused")
    public void setResolver(TranslationResolver resolver) {
        this.resolver = Objects.requireNonNull(resolver);
    }

    @Override
    public @Nullable Locale resolveLocale(String langId) {
        if (localeResolver == null) return null;
        return localeCache.computeIfAbsent(langId, localeResolver::getLocale);
    }

    @Override
    public void resetLocaleCache() {
        localeCache.clear();
    }

    @Override
    public Translation get(Context context, String key, String language) {
        return resolver.get(context, texts, key, language);
    }

}
