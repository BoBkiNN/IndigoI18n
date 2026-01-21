package xyz.bobkinn.indigoi18n;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.bobkinn.indigoi18n.context.Context;
import xyz.bobkinn.indigoi18n.context.impl.RenderTypeContext;
import xyz.bobkinn.indigoi18n.context.impl.LangKeyContext;
import xyz.bobkinn.indigoi18n.context.impl.SourceContext;
import xyz.bobkinn.indigoi18n.data.Translation;
import xyz.bobkinn.indigoi18n.data.TranslationInfo;
import xyz.bobkinn.indigoi18n.render.RenderType;
import xyz.bobkinn.indigoi18n.render.Renderer;

import java.util.List;
import java.util.Locale;


/**
 * Declares main high-level API
 * @see IndigoI18n minimal implementation
 */
public interface I18nEngine {

    /**
     * Resolves translation by key and language
     */
    Translation get(Context context, String key, String language);

    /**
     * Returns renderer that can be used to output and format {@link T}
     * @param <T> type of output object
     */
    <T> Renderer<T> getRenderer(RenderType<T> ft);

    /**
     * Returns info about translation
     * @return {@link TranslationInfo} containing source
     */
    TranslationInfo infoFor(String lang, String key);

    default Context newContext(@Nullable TranslationInfo info, String lang, String key) {
        var ctx = new Context(null);
        ctx.setI18n(this);
        if (info != null) {
            ctx.set(new SourceContext(info.source()));
        }
        ctx.set(new LangKeyContext(lang, key, resolveLocale(lang)));
        return ctx;
    }

    default Context newContext(String lang, String key) {
        return newContext(null, lang, key);
    }

    /**
     * Computes context, gets {@link Translation}, merges context overrides, resolves text from translation,
     * performs rendering using {@link Renderer}
     * @param ft output (format) type
     * @param ctx context to pass
     * @param lang language id
     * @param key translation key
     * @param args formatting arguments
     * @param <T> output type
     */
    default <T> T parse(RenderType<T> ft, @Nullable Context ctx, String lang, String key, List<Object> args) {
        var targetCtx = computeContext(ctx, lang, key);
        var renderer = getRenderer(ft);
        if (renderer == null) throw new IllegalArgumentException("Unknown renderer for output "+ft);
        // set current renderer type
        targetCtx.set(new RenderTypeContext(ft));
        var tr = get(targetCtx, key, lang);
        if (tr == null) return renderer.onNullTranslation(targetCtx, key);

        var ovr = tr.getContextOverride();
        if (ovr != null) targetCtx.merge(ovr, true);
        return renderer.render(targetCtx, tr.resolve(targetCtx), args);
    }

    /**
     * Collects possible translation information and builds context.<br>
     * If context is null, new context is created.<br>
     * If context is not {@link Context#isComplete() complete} fresh context is created and merged with passed one (no overrides)<br>
     * If source information is found, {@link SourceContext} added to resulting context.
     */
    default Context computeContext(@Nullable Context ctx, String lang, String key) {
        var info = infoFor(lang, key);
        Context targetCtx;
        if (ctx == null) {
            targetCtx = newContext(info, lang, key);
        } else if (!ctx.isComplete()) {
            // passed context is incomplete (missing root data)
            // so create correct context and merge with passed without overriding
            targetCtx = newContext(info, lang, key);
            targetCtx.merge(ctx, false);
        } else {
            targetCtx = ctx;
        }
        // compute source context if available
        if (info != null) targetCtx.compute(SourceContext.class, () -> new SourceContext(info.source()));
        return targetCtx;
    }

    /**
     * Locale resolver used in this I18n instance
     */
    @NotNull LocaleResolver getLocaleResolver();

    /**
     * Resets locale cache
     */
    @SuppressWarnings("unused")
    void resetLocaleCache();

    /**
     * Resolves language by id using LocaleResolver and caches it for future accesses.
     * @param langId language id
     * @return null if no language resolved
     * @see #resolveLocale(String)
     * @see #getLocaleResolver()
     */
    @Nullable Locale resolveLocale(String langId);
}
