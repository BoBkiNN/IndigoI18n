package xyz.bobkinn.indigoi18n.source.impl.gson;

import com.google.gson.*;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public abstract class DiscriminatorAdapter<T>
        implements JsonSerializer<T>, JsonDeserializer<T> {
    private final String discriminatorField;

    public static final String DEFAULT_FIELD_NAME = "type";

    @SuppressWarnings("unused")
    public DiscriminatorAdapter() {
        this(DEFAULT_FIELD_NAME);
    }

    @Override
    public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        JsonElement discriminator = obj.get(discriminatorField);

        if (discriminator == null) throw new JsonParseException(
                    "Missing discriminator field: " + discriminatorField
            );


        String type = discriminator.getAsString();
        Class<? extends T> targetClass = classFor(type);

        if (targetClass == null) throw new JsonParseException("Unknown discriminator value: " + type);
        return context.deserialize(json, targetClass);
    }

    @SuppressWarnings("unchecked")
    @Override
    public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
        Class<? extends T> srcClass = (Class<? extends T>) src.getClass();
        String type = typeOf(srcClass);
        if (type == null) throw new JsonParseException(
                    "No discriminator mapping for class: " + srcClass.getName()
            );

        JsonObject obj = context.serialize(src, srcClass).getAsJsonObject();
        obj.addProperty(discriminatorField, type);
        return obj;
    }

    protected abstract Class<? extends T> classFor(String type);

    protected abstract String typeOf(Class<? extends T> clazz);

    public static class MapDiscriminatorAdapter<T> extends DiscriminatorAdapter<T> {
        private final Map<String, Class<? extends T>> typeToCls;
        private final Map<Class<? extends T>, String> clsToType;

        public MapDiscriminatorAdapter(String fieldDiscriminator, @NotNull Map<String, Class<? extends T>> typeToCls) {
            super(fieldDiscriminator);
            this.typeToCls = typeToCls;
            var t = new HashMap<Class<? extends T>, String>(typeToCls.size());
            for (var e : typeToCls.entrySet()) {
                if (t.put(e.getValue(), e.getKey()) != null) {
                    throw new IllegalArgumentException("Duplicate class mapping: " + e.getValue());
                }
            }
            clsToType = t;
        }

        public MapDiscriminatorAdapter(@NotNull Map<String, Class<? extends T>> typeToCls) {
            this(DEFAULT_FIELD_NAME, typeToCls);
        }

        @Override
        protected Class<? extends T> classFor(String type) {
            return typeToCls.get(type);
        }

        @Override
        protected String typeOf(Class<? extends T> clazz) {
            return clsToType.get(clazz);
        }
    }

    @Contract("_ -> new")
    public static <T> @NotNull MapDiscriminatorAdapter<T> mapping(@NotNull Map<String, Class<? extends T>> typeToCls) {
        return new MapDiscriminatorAdapter<>(typeToCls);
    }

}
