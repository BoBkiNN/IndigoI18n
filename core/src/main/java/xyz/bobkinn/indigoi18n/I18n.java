package xyz.bobkinn.indigoi18n;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import xyz.bobkinn.indigoi18n.data.TranslationInfo;
import xyz.bobkinn.indigoi18n.format.I18nFormat;
import xyz.bobkinn.indigoi18n.resolver.BasicTranslationResolver;
import xyz.bobkinn.indigoi18n.resolver.TranslationResolver;
import xyz.bobkinn.indigoi18n.source.TranslationSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * I18n with no formats or its access methods.<br>
 * Uses default resolver and do not include any translations
 * @see StringI18n
 */
public class I18n implements I18nBase {
    protected final Translations translations;
    @Getter
    private @NotNull TranslationResolver resolver;
    private final Map<Class<?>, I18nFormat<?>> formats;

    public I18n() {
        translations = new Translations();
        resolver = new BasicTranslationResolver();
        formats = new HashMap<>();
        addDefaultFormats();
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
        translations.load(source);
    }

    public void unload(TranslationSource source) {
        translations.unload(source);
    }

    public List<TranslationSource> sources() {
        return translations.sources();
    }

    @Override
    public TranslationInfo infoFor(String lang, String key) {
        return translations.infoFor(key, lang);
    }

    public void setResolver(TranslationResolver resolver) {
        this.resolver = Objects.requireNonNull(resolver);
    }

    @Override
    public String get(String key, String language) {
        return resolver.get(translations, key, language);
    }

}
