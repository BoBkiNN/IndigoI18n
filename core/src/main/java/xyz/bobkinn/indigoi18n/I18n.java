package xyz.bobkinn.indigoi18n;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import xyz.bobkinn.indigoi18n.data.TranslationInfo;
import xyz.bobkinn.indigoi18n.format.I18nFormat;
import xyz.bobkinn.indigoi18n.format.impl.StringI18nFormat;
import xyz.bobkinn.indigoi18n.format.impl.StringI18nMixin;
import xyz.bobkinn.indigoi18n.resolver.BasicTranslationResolver;
import xyz.bobkinn.indigoi18n.resolver.TranslationResolver;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

// TODO should i split this class into real empty i18n and this default
/**
 * Basic I18n module with string format
 */
public class I18n implements StringI18nMixin {
    private final Translations translations;
    @Getter
    private @NotNull TranslationResolver resolver;
    private final Map<Class<?>, I18nFormat<?>> formats;

    public I18n(Translations translations) {
        this.translations = translations;
        resolver = new BasicTranslationResolver();
        formats = new HashMap<>();
        addDefaultFormats();
    }

    public <T> void addFormat(Class<T> cls, I18nFormat<T> format) {
        formats.put(cls, format);
    }

    protected void addDefaultFormats() {
        addFormat(String.class, new StringI18nFormat(translations.getCache()));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> I18nFormat<T> getFormat(Class<T> cls) {
        var f = (I18nFormat<T>) formats.get(cls);
        return Objects.requireNonNull(f, "Unknown format for "+cls.getSimpleName());
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
