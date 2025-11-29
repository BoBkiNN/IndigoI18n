package xyz.bobkinn.indigoi18n.template;

import xyz.bobkinn.indigoi18n.data.ParsedEntry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public abstract class TemplateFormatter<O> {
    protected final Map<Class<?>, ArgumentConverter<?, O>> converters = new HashMap<>();
    protected final Map<Class<?>, Function<Object, O>> reprCreators = new HashMap<>();

    public TemplateFormatter() {
        registerDefaultConverters();
        registerDefaultReprCreators();
    }

    protected abstract void registerDefaultConverters();

    @SuppressWarnings("unchecked")
    public <T> void registerRepr(Class<T> cls, Function<T, O> f) {
        reprCreators.put(cls, (Function<Object, O>) f);
    }

    protected void registerDefaultReprCreators() {
        // represent number as-is
        registerRepr(Number.class, n -> createText(n.toString()));
        // anything other will be quoted as default behaviour
    }

    @SuppressWarnings("unchecked")
    public <T> ArgumentConverter<T, O> getConverter(T value) {
        return (ArgumentConverter<T, O>) converters.get(value.getClass());
    }

    protected Function<Object, O> resolveReprCreator(Class<?> cls) {
        // exact match
        var f = reprCreators.get(cls);
        if (f != null) return f;
        // by base class
        for (var e : reprCreators.entrySet()) {
            if (e.getKey().isAssignableFrom(cls)) {
                return e.getValue();
            }
        }
        return null;
    }

    public static String stringRepr(String value) {
        return Utils.quote(value);
    }

    public abstract O createText(String value);

    public abstract O format(ParsedEntry entry, List<Object> params);


    public O createRepr(Object object) {
        if (object == null) return createText(stringRepr("null"));
        var cls = object.getClass();
        var creator = resolveReprCreator(cls);
        if (creator != null) return creator.apply(object);
        return createText(stringRepr(String.valueOf(object)));
    }
}
