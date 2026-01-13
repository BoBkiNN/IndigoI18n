package xyz.bobkinn.indigoi18n;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import xyz.bobkinn.indigoi18n.codegen.GenStaticDefault;
import xyz.bobkinn.indigoi18n.format.FormatType;
import xyz.bobkinn.indigoi18n.format.impl.StringI18nFormat;
import xyz.bobkinn.indigoi18n.format.impl.StringI18nMixin;


/**
 * Simple I18n class. It uses only {@link StringI18nFormat} formatter and implements {@link StringI18nMixin}.
 * @see StringI18nFormat
 */
@GenStaticDefault(name = "Indigo", creator = "create")
public class StringI18n extends IndigoI18n implements StringI18nMixin {

    @Contract(" -> new")
    public static @NotNull StringI18n create() {
        return new StringI18n();
    }

    @Override
    protected void addDefaultFormats() {
        addFormat(FormatType.STRING_FORMAT_TYPE, StringI18nFormat::new);
    }
}
