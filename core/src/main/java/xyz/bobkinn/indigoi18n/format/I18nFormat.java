package xyz.bobkinn.indigoi18n.format;

import lombok.RequiredArgsConstructor;
import xyz.bobkinn.indigoi18n.context.Context;
import xyz.bobkinn.indigoi18n.data.TemplateCache;
import xyz.bobkinn.indigoi18n.Translations;

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
     * Called only when translation resolver returned null.<br>
     * Default implementation produces object with key text
     * @param ctx translation context
     * @param key key passed to parse
     * @return value that will be returned by parse
     */
    public T onNullTranslation(@SuppressWarnings("unused") Context ctx, String key) {
        return produce(key);
    }

    /**
     * Replaces arguments inside input original object
     */
    public abstract T replaceArguments(Context ctx, T input, List<Object> args);

    /**
     * Uses text to produce object with arguments replaced.
     * @param text original text
     * @param args arguments
     */
    public T format(Context ctx, String text, List<Object> args) {
        var obj = produce(text);
        return replaceArguments(ctx, obj, args);
    }

}
