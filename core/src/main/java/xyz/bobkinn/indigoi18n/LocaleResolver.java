package xyz.bobkinn.indigoi18n;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public interface LocaleResolver {
    Locale getLocale(String lang);

    LocaleResolver DEFAULT = LocaleResolver::parseLocale;

    static @Nullable Locale parseLocale(final @NotNull String string) {
        final String[] segments = string.split("_", 3); // language_country_variant
        final int length = segments.length;
        if (length == 1) {
            return LocaleUtil.of(string); // language
        } else if (length == 2) {
            return LocaleUtil.of(segments[0], segments[1]); // language + country
        } else if (length == 3) {
            return LocaleUtil.of(segments[0], segments[1], segments[2]); // language + country + variant
        }
        return null;
    }
}
