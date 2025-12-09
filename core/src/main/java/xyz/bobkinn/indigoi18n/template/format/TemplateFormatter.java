package xyz.bobkinn.indigoi18n.template.format;

import org.jetbrains.annotations.Nullable;
import xyz.bobkinn.indigoi18n.data.ParsedEntry;
import xyz.bobkinn.indigoi18n.template.arg.ArgumentConverter;
import xyz.bobkinn.indigoi18n.template.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public abstract O format(ParsedEntry entry, List<Object> params);


    public O createRawRepr(Object object) {
        if (object == null) return createText(stringRawRepr("null"));
        var cls = object.getClass();
        var creator = resolveRawReprCreator(cls);
        if (creator != null) return creator.apply(object);
        return createText(stringRawRepr(String.valueOf(object)));
    }
}
