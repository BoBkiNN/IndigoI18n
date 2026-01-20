package xyz.bobkinn.indigoi18n;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * Locale resolver is used to convert language id string into instance of Locale.
 * This is used to correctly lookup plurals rules for that language.
 * @see #parseLocale(String) default implementation
 */
public interface LocaleResolver {

    /**
     * Get Locale instance from language id
     */
    Locale getLocale(String lang);

    /**
     * Get language id from Locale
     */
    String getId(Locale locale);

    LocaleResolver DEFAULT = new LocaleResolver() {
        @Override
        public Locale getLocale(String lang) {
            return parseLocale(lang);
        }

        @Override
        public String getId(Locale locale) {
            return formatLocale(locale);
        }
    };

    /**
     * Default implementation of {@link LocaleResolver#getLocale}<br>
     * It uses format {@code language_country_variant}
     */
    static @Nullable Locale parseLocale(final @NotNull String string) {
        final String[] segments = string.split("_", 3); // language_country_variant
        final int length = segments.length;
        if (length == 1) {
            return new Locale(string); // language
        } else if (length == 2) {
            return new Locale(segments[0], segments[1]); // language + country
        } else if (length == 3) {
            return new Locale(segments[0], segments[1], segments[2]); // language + country + variant
        }
        return null;
    }

    /**
     * Default implementation of {@link LocaleResolver#getId(Locale)}<br>
     * It uses format {@code language_country_variant}
     */
    static @NotNull String formatLocale(final @NotNull Locale locale) {
        String language = locale.getLanguage();
        String country = locale.getCountry().toLowerCase();
        String variant = locale.getVariant().toLowerCase();

        if (!variant.isEmpty()) {
            return language + "_" + country + "_" + variant;
        } else if (!country.isEmpty()) {
            return language + "_" + country;
        } else {
            return language;
        }
    }
}
