package xyz.bobkinn.indigoi18n;

import xyz.bobkinn.indigoi18n.format.impl.StringI18nFormat;
import xyz.bobkinn.indigoi18n.format.impl.StringI18nMixin;


/**
 * Simple I18n class. It uses only {@link StringI18nFormat} formatter and implements {@link StringI18nMixin}.
 * @see StringI18nFormat
 */
public class StringI18n extends I18n implements StringI18nMixin {

    @Override
    protected void addDefaultFormats() {
        addFormat(String.class, new StringI18nFormat(translations.getCache()));
    }
}
