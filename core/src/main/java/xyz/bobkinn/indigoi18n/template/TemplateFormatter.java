package xyz.bobkinn.indigoi18n.template;

import xyz.bobkinn.indigoi18n.data.ParsedEntry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public abstract class TemplateFormatter<O> {
    protected final Map<Class<?>, ArgumentConverter<?, O>> converters = new HashMap<>();
    protected final Map<Class<?>, Function<Object, O>> reprCreators = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T> ArgumentConverter<T, O> getConverter(T value) {
        return (ArgumentConverter<T, O>) converters.get(value.getClass());
    }

    public static String stringRepr(String value) {
        return TemplateFormatters.pyQuote(value);
    }

    public abstract O createText(String value);

    public abstract O format(ParsedEntry entry, List<Object> params);


    public O createRepr(Object object) {
        if (object == null) return createText(stringRepr("null"));
        var cls = object.getClass();
        var creator = reprCreators.get(cls);
        if (creator != null) return creator.apply(object);
        return createText(stringRepr(String.valueOf(object)));
    }
}
