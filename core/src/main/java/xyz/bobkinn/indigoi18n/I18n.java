package xyz.bobkinn.indigoi18n;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import xyz.bobkinn.indigoi18n.resolver.BasicTranslationResolver;
import xyz.bobkinn.indigoi18n.resolver.TranslationResolver;

import java.util.Objects;

public class I18n {
    private final Translations translations;
    @Getter
    private @NotNull TranslationResolver resolver;

    public I18n(Translations translations) {
        this.translations = translations;
        resolver = new BasicTranslationResolver();
    }

    public void setResolver(TranslationResolver resolver) {
        this.resolver = Objects.requireNonNull(resolver);
    }

    public String get(String key, String language) {
        return resolver.get(translations, key, language);
    }

}
