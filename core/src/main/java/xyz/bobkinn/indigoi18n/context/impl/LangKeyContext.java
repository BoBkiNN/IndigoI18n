package xyz.bobkinn.indigoi18n.context.impl;

import lombok.Data;
import org.jetbrains.annotations.Nullable;
import xyz.bobkinn.indigoi18n.context.ContextEntry;

import java.util.Locale;

@Data
public class LangKeyContext implements ContextEntry {
    private final String lang;
    private final String key;
    private final @Nullable Locale resolvedLocale;

    public LangKeyContext withLocale(Locale locale) {
        return new LangKeyContext(lang, key, locale);
    }
}
