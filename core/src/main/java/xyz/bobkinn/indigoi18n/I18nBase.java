package xyz.bobkinn.indigoi18n;

import xyz.bobkinn.indigoi18n.data.TranslationInfo;
import xyz.bobkinn.indigoi18n.format.I18nFormat;

import java.util.List;


public interface I18nBase {
    String get(String key, String language);

    <T> I18nFormat<T> getFormat(Class<T> cls);

    TranslationInfo infoFor(String lang, String key);

    default <T> T parse(Class<T> cls, String lang, String key, List<Object> args) {
        return getFormat(cls).format(infoFor(lang, key), get(key, lang), args);
    }
}
