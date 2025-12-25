package xyz.bobkinn.indigoi18n.source.impl;

import com.google.gson.*;
import lombok.RequiredArgsConstructor;
import net.xyzsd.plurals.PluralCategory;
import xyz.bobkinn.indigoi18n.context.Context;
import xyz.bobkinn.indigoi18n.data.PluralTranslation;

import java.lang.reflect.Type;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * Used to ignore key case
 */
@RequiredArgsConstructor
public class PluralTranslationAdapter implements JsonDeserializer<PluralTranslation> {
    private final String typeFieldName;
    private final ContextParser contextParser;

    public PluralTranslationAdapter(ContextParser parser) {
        this(DiscriminatorAdapter.DEFAULT_FIELD_NAME, parser);
    }

    @Override
    public PluralTranslation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();

        Map<PluralCategory, String> plurals = new EnumMap<>(PluralCategory.class);

        for (Map.Entry<String, JsonElement> e : obj.entrySet()) {
            String key = e.getKey();
            if (Objects.equals(key, typeFieldName)) continue;
            if (Objects.equals(key, ContextParser.FIELD_NAME)) continue;

            PluralCategory category;
            try {
                category = PluralCategory.valueOf(key.toUpperCase());
            } catch (IllegalArgumentException ex) {
                throw new JsonParseException(
                        "Unknown plural category: " + key
                );
            }

            plurals.put(category, e.getValue().getAsString());
        }

        Context ctx;
        if (obj.has(ContextParser.FIELD_NAME)) {
            ctx = contextParser.parse(obj.getAsJsonObject(ContextParser.FIELD_NAME));
        } else ctx = null;

        return new PluralTranslation(plurals, ctx);
    }
}
