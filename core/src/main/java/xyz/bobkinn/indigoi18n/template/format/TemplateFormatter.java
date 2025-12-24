package xyz.bobkinn.indigoi18n.template.format;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.bobkinn.indigoi18n.context.Context;
import xyz.bobkinn.indigoi18n.context.impl.InlineContext;
import xyz.bobkinn.indigoi18n.context.impl.LangKeyContext;
import xyz.bobkinn.indigoi18n.data.ParsedEntry;
import xyz.bobkinn.indigoi18n.template.InlineTranslation;
import xyz.bobkinn.indigoi18n.template.arg.ArgumentConverter;
import xyz.bobkinn.indigoi18n.template.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public abstract class TemplateFormatter<O> {
    /**
     * Map of class type to its converter. null key may be mapped to null value converter
     */
    protected final Map<Class<?>, ArgumentConverter<?, O>> converters = new HashMap<>();
    protected final Map<Class<?>, Function<Object, O>> rawReprCreators = new HashMap<>();

    public TemplateFormatter() {
        registerDefaultConverters();
        registerDefaultReprCreators();
    }

    protected abstract void registerDefaultConverters();

    @SuppressWarnings("unchecked")
    public <T> void registerRawRepr(Class<T> cls, Function<T, O> f) {
        rawReprCreators.put(cls, (Function<Object, O>) f);
    }

    protected void registerDefaultReprCreators() {
        // represent number as-is
        registerRawRepr(Number.class, n -> createText(n.toString()));
        // anything other will be quoted as default behaviour
    }

    /**
     * Returns converter based value class. <br>
     * If value is null, then null is key for converters map and null converter is returned if it set.
     * @param value value to find converter for
     * @return argument converter for this value of argument
     */
    @SuppressWarnings("unchecked")
    public <T> ArgumentConverter<T, O> getConverter(@Nullable T value) {
        var key = value == null ? null : value.getClass();
        return (ArgumentConverter<T, O>) converters.get(key);
    }

    public <T> void addConverter(@Nullable Class<? extends T> cls, ArgumentConverter<T, O> converter) {
        converters.put(cls, Objects.requireNonNull(converter, "converter"));
    }

    @SuppressWarnings("unused")
    public boolean removeConverter(@Nullable Class<?> cls) {
        return converters.remove(cls) != null;
    }

    protected Function<Object, O> resolveRawReprCreator(Class<?> cls) {
        // exact match
        var f = rawReprCreators.get(cls);
        if (f != null) return f;
        // by base class
        for (var e : rawReprCreators.entrySet()) {
            if (e.getKey().isAssignableFrom(cls)) {
                return e.getValue();
            }
        }
        return null;
    }

    public static String stringRawRepr(String value) {
        return Utils.quote(value);
    }

    public abstract O createText(String value);

    public abstract O format(Context ctx, ParsedEntry entry, List<Object> params);


    public O createRawRepr(Object object) {
        if (object == null) return createText(stringRawRepr("null"));
        var cls = object.getClass();
        var creator = resolveRawReprCreator(cls);
        if (creator != null) return creator.apply(object);
        return createText(stringRawRepr(String.valueOf(object)));
    }

    /**
     * Uses {@link xyz.bobkinn.indigoi18n.I18nBase#parse(Class, Context, String, String, List) parse method}
     * to parse and return text specified by key and translation inside {@link InlineTranslation}.<br>
     * Each inlining decreases remaining depth in passed sub-context.<br>
     * When no inlining were previously done, remaining depth is equals to maxDepth from {@link InlineTranslation}.<br>
     * If remaining depth <= 0, exception is thrown.<br>
     * If context tree does not contain {@link LangKeyContext} when language override not declared,
     * exception is thrown.
     * @param cls output class
     * @param params list of arguments
     */
    protected O formatInline(@SuppressWarnings("SameParameterValue") Class<O> cls,
                             @NotNull InlineTranslation inline,
                             @NotNull Context ctx, List<Object> params) {
        int cd = ctx.getOptional(InlineContext.class)
                .map(InlineContext::getRemainingDepth)
                .orElse(inline.getMaxDepth());
        var key = inline.getKey();
        if (cd <= 0) {
            // no remaining depth
            throw new IllegalStateException("Depth limit exceeded");
        }
        var i18n = ctx.getI18n();
        String targetLang;
        if (inline.getLang() != null) {
            targetLang = inline.getLang();
        } else {
            targetLang = ctx.resolveOptional(LangKeyContext.class)
                    .map(LangKeyContext::getLang)
                    .orElseThrow(() -> new IllegalStateException("No language in current context tree"));
        }
        var sub = ctx.sub(targetLang, key, i18n.resolveLocale(targetLang));
        sub.set(new InlineContext(cd-1));
        /* Set lang & key in this context. We still can find original language in key by using parent context.
        With this we ensure that all deeper translations share target language
        unless override set at deeper InlineTranslation */
        return i18n.parse(cls, sub, targetLang, key, params);
    }
}
