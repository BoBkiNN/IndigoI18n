package xyz.bobkinn.indigoi18n;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.bobkinn.indigoi18n.context.Context;
import xyz.bobkinn.indigoi18n.context.impl.LangKeyContext;
import xyz.bobkinn.indigoi18n.context.impl.SourceContext;
import xyz.bobkinn.indigoi18n.data.TranslationInfo;
import xyz.bobkinn.indigoi18n.format.I18nFormat;

import java.util.List;


public interface I18nBase {
    String get(Context context, String key, String language);

    <T> I18nFormat<T> getFormat(Class<T> cls);

    TranslationInfo infoFor(String lang, String key);

    default Context newContext(@Nullable TranslationInfo info, String lang, String key) {
        var ctx = new Context(null, this);
        if (info != null) {
            ctx.set(new SourceContext(info.source()));
        }
        ctx.set(new LangKeyContext(lang, key));
        return ctx;
    }

    default Context newContext(String lang, String key) {
        return newContext(null, lang, key);
    }

    default <T> T parse(Class<T> cls, @Nullable Context ctx, String lang, String key, List<Object> args) {
        var info = infoFor(lang, key);
        Context targetCtx;
        if (ctx == null) {
            targetCtx = newContext(info, lang, key);
        } else if (!ctx.isComplete()) {
            // passed context is incomplete (missing root data)
            // so create correct context and merge with passed
            targetCtx = newContext(info, lang, key);
            targetCtx.merge(ctx);
        } else {
            targetCtx = ctx;
        }
        // compute source context if available
        if (info != null) targetCtx.compute(SourceContext.class, () -> new SourceContext(info.source()));
        return getFormat(cls).format(targetCtx, get(targetCtx, key, lang), args);
    }

    /**
     * Locale resolver used in this I18n instance
     */
    @NotNull LocaleResolver getLocaleResolver();
}
