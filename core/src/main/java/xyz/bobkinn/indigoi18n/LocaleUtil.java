package xyz.bobkinn.indigoi18n;

import java.util.Locale;
import java.util.Objects;

public final class LocaleUtil {

    private LocaleUtil() {}

    public static Locale of(String language) {
        Objects.requireNonNull(language, "language");
        return new Locale(language);
    }

    public static Locale of(String language, String country) {
        Objects.requireNonNull(language, "language");
        Objects.requireNonNull(country, "country");
        return new Locale(language, country);
    }

    public static Locale of(String language, String country, String variant) {
        Objects.requireNonNull(language, "language");
        Objects.requireNonNull(country, "country");
        Objects.requireNonNull(variant, "variant");
        return new Locale(language, country, variant);
    }
}
