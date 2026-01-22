package xyz.bobkinn.indigoi18n.template.format;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.bobkinn.indigoi18n.I18nEngine;
import xyz.bobkinn.indigoi18n.context.Context;
import xyz.bobkinn.indigoi18n.context.impl.RenderTypeContext;
import xyz.bobkinn.indigoi18n.context.impl.InlineContext;
import xyz.bobkinn.indigoi18n.context.impl.LangKeyContext;
import xyz.bobkinn.indigoi18n.data.ParsedEntry;
import xyz.bobkinn.indigoi18n.render.RenderType;
import xyz.bobkinn.indigoi18n.template.InlineTranslation;
import xyz.bobkinn.indigoi18n.template.arg.ArgConverters;
import xyz.bobkinn.indigoi18n.template.arg.ArgumentConverter;
import xyz.bobkinn.indigoi18n.template.Utils;
import xyz.bobkinn.indigoi18n.template.arg.TemplateArgument;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * TemplateFormatter is used to produce output object O from {@link ParsedEntry template parts}.<br>
 * Usually it performs inlining, argument representation and conversion (using {@link ArgumentConverter})
 * and building output object.
 * @param <O> output type
 * @see ArgumentConverter
 * @see StringTemplateFormatter string implementation
 * @see #format(Context, ParsedEntry, List) main entrypoint
 * @see ParsedEntry
 */
public abstract class TemplateFormatter<O> {
    /**
     * Map of class type to its converter. null key may be mapped to null value converter
     */
    protected final Map<Class<?>, ArgumentConverter<?, O>> converters = new HashMap<>();
    /**
     * !r representation handlers
     */
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
     * Returns converter based value class. Used when exact type converter is needed<br>
     * If value is null, then null is key for converters map and null converter is returned if it set.
     * @param value value to find converter for
     * @return argument converter for this value of argument
     */
    @SuppressWarnings("unchecked")
    public <T> ArgumentConverter<T, O> getConverter(@Nullable T value) {
        var key = value == null ? null : value.getClass();
        return (ArgumentConverter<T, O>) converters.get(key);
    }

    public <T> void putConverter(@Nullable Class<? extends T> cls, ArgumentConverter<T, O> converter) {
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

    /**
     * Method used to create plain text from string value
     * @return new text object
     */
    public abstract O createText(String value);

    /**
     * Method used to create output for null arguments.
     * @param format format to possibly use to produce null text
     */
    public abstract O formatNull(Context ctx, FormatPattern format);

    /**
     * Format common representations as strings. Modes r, h, H, s are handled
     * @param arg argument that is being represented
     * @param value passed argument value
     * @param rawReprCreator function to produce string from text
     * @return null if no representation performed
     */
    protected String formatRepresentation(Context ctx, TemplateArgument arg, Object value,
                                          Function<Object, String> rawReprCreator) {
        var format = Objects.requireNonNull(arg.getFormat(), "Missing format in argument");
        if (arg.isRepr('r')) {
            var rawRepr = rawReprCreator.apply(value);
            return ArgConverters.STRING_CONVERTER.format(ctx, rawRepr, format);
        }
        if (arg.isRepr('h', 'H')) {
            // probably ok delegation to %h/H
            return ArgConverters.STRING_CONVERTER.format(ctx, String.format("%"+arg.getRepr(), value), format);
        }
        if (arg.isRepr('s') && (value == null || value.getClass() != String.class)) {
            // !s converts any object (except string) to string and then formats using string converter
            return ArgConverters.STRING_CONVERTER.format(ctx, String.valueOf(value), format);
        }
        return null;
    }

    /**
     * Produces output from parsed entries and list of passed arguments
     * @param ctx context
     * @param entry parsed input
     * @param params list of passed arguments
     * @return output object
     */
    public abstract O format(Context ctx, ParsedEntry entry, List<Object> params);


    public O createRawRepr(Object object) {
        if (object == null) return createText(stringRawRepr("null"));
        var cls = object.getClass();
        var creator = resolveRawReprCreator(cls);
        if (creator != null) return creator.apply(object);
        return createText(stringRawRepr(String.valueOf(object)));
    }

    /**
     * Uses {@link I18nEngine#parse(RenderType, Context, String, String, List) parse method}
     * to parse and return text specified by key and translation inside {@link InlineTranslation}.<br>
     * Each inlining decreases remaining depth in passed sub-context.<br>
     * When no inlining were previously done, remaining depth is equals to maxDepth from {@link InlineTranslation}.<br>
     * If remaining depth &lt;= 0, exception is thrown.<br>
     * If context tree does not contain {@link LangKeyContext} when language override not declared,
     * exception is thrown.
     * @param ft output type
     * @param params list of arguments
     */
    public O formatInline(@SuppressWarnings("SameParameterValue") RenderType<O> ft,
                             @NotNull InlineTranslation inline,
                             @NotNull Context ctx, List<Object> params) {
        // get remaining depth or use specified max depth from InlineTranslation
        int cd = ctx.getOptional(InlineContext.class, InlineContext::getRemainingDepth)
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
        /* Set lang & key in this context. We still can find original language in key by using parent context.
        With this we ensure that all deeper translations share target language
        unless override set at deeper InlineTranslation */
        var sub = ctx.sub(targetLang, key, i18n.resolveLocale(targetLang));
        sub.set(new InlineContext(cd-1));
        return i18n.parse(ft, sub, targetLang, key, params);
    }

    /**
     * Resolves converter by type distance, closer class match returns closest converter
     */
    @SuppressWarnings("unchecked")
    public <T> ArgumentConverter<T, O> resolveConverter(@Nullable T value) {
        // null handling stays exactly as you want
        if (value == null) {
            return (ArgumentConverter<T, O>) converters.get(null);
        }

        Class<?> valueClass = value.getClass();

        ArgumentConverter<?, O> best = null;
        int bestDistance = Integer.MAX_VALUE;

        for (var e : converters.entrySet()) {
            Class<?> key = e.getKey();
            if (key == null) continue;

            if (!key.isAssignableFrom(valueClass)) continue;

            int distance = typeDistance(valueClass, key);
            if (distance < bestDistance) {
                bestDistance = distance;
                best = e.getValue();
            }
        }

        return (ArgumentConverter<T, O>) best;
    }

    private static int typeDistance(@NotNull Class<?> from, Class<?> to) {
        if (from == to) return 0;

        int best = Integer.MAX_VALUE;

        // walk superclass chain
        int depth = 0;
        for (Class<?> c = from; c != null; c = c.getSuperclass()) {
            if (c == to) {
                best = depth;
                break;
            }
            depth++;
        }

        // check interfaces (penalized)
        int ifaceDistance = interfaceDistance(from, to, 0);
        if (ifaceDistance >= 0) {
            // add penalty so class inheritance always wins at same depth
            ifaceDistance += 100;
            best = Math.min(best, ifaceDistance);
        }

        return best;
    }

    private static int interfaceDistance(Class<?> from, Class<?> target, int depth) {
        for (Class<?> iFace : from.getInterfaces()) {
            if (iFace == target) return depth + 1;

            int d = interfaceDistance(iFace, target, depth + 1);
            if (d >= 0) return d;
        }

        Class<?> superClass = from.getSuperclass();
        if (superClass != null) {
            return interfaceDistance(superClass, target, depth + 1);
        }

        return -1;
    }

    /**
     * Resolves render type from context using {@link RenderTypeContext} and casts it to {@code FormatType<O>}
     * @return null if no render type in context or context is null
     * @throws IllegalStateException if incompatible type found
     */
    public @Nullable RenderType<O> resolveRenderType(Context ctx) {
        if (ctx == null) return null;
        var rtc = ctx.resolve(RenderTypeContext.class);
        if (rtc == null) return null;
        try {
            //noinspection unchecked
            return (RenderType<O>) rtc.getRenderType();
        } catch (ClassCastException e) {
            throw new IllegalStateException("Incompatible render type %s found in context %s"
                    .formatted(rtc.getRenderType(), ctx), e);
        }
    }
}
