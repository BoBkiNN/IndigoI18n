package xyz.bobkinn.indigoi18n.format;

import lombok.RequiredArgsConstructor;
import xyz.bobkinn.indigoi18n.TemplateCache;
import xyz.bobkinn.indigoi18n.Translations;
import xyz.bobkinn.indigoi18n.data.TranslationInfo;

import java.util.List;


/**
 * I18n format is used to work with specified output type
 * @param <T> output text type
 * @see xyz.bobkinn.indigoi18n.format.impl.StringI18nFormat format for String
 */
@RequiredArgsConstructor
public abstract class I18nFormat<T> {
    /**
     * Cache that can be used
     */
    protected final TemplateCache cache;

    /**
     * Produces object from original text
     * @param text original text from {@link Translations}
     */
    public abstract T produce(String text);

    /**
     * Replaces arguments inside input original object
     */
    public abstract T replaceArguments(TranslationInfo info, T input, List<Object> args);

    /**
     * Produces new object and replaces arguments
     * @param text original text
     * @param args arguments
     */
    public T format(TranslationInfo info, String text, List<Object> args) {
        var obj = produce(text);
        return replaceArguments(info, obj, args);
    }

}
