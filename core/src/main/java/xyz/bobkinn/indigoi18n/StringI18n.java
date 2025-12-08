package xyz.bobkinn.indigoi18n;

import xyz.bobkinn.indigoi18n.format.impl.StringI18nFormat;
import xyz.bobkinn.indigoi18n.format.impl.StringI18nMixin;


public class StringI18n extends I18n implements StringI18nMixin {

    @Override
    protected void addDefaultFormats() {
        addFormat(String.class, new StringI18nFormat(translations.getCache()));
    }
}
