package xyz.bobkinn.indigoi18n.source.impl.gson;

import com.google.gson.*;
import lombok.RequiredArgsConstructor;
import net.xyzsd.plurals.PluralCategory;
import xyz.bobkinn.indigoi18n.context.Context;
import xyz.bobkinn.indigoi18n.data.PluralTranslation;

import java.lang.reflect.Type;
import java.util.EnumMap;
import java.util.Map;

/**
 * Used to ignore key case
 */
@RequiredArgsConstructor
public class PluralTranslationAdapter implements JsonDeserializer<PluralTranslation> {
    private final ContextParser contextParser;

    @Override
    public PluralTranslation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();

        Map<PluralCategory, String> plurals = new EnumMap<>(PluralCategory.class);

        for (var cat : PluralCategory.values()) {
            var name = cat.name().toLowerCase();
            for (var e : obj.entrySet()) {
                if (e.getKey().toLowerCase().equals(name)) {
                    // match
                    plurals.put(cat, e.getValue().getAsString());
                }
            }
        }

        if (plurals.isEmpty()) {
            throw new JsonParseException("No plural categories supplied");
        }

        Context ctx;
        if (obj.has(ContextParser.FIELD_NAME)) {
            ctx = contextParser.parse(obj.getAsJsonObject(ContextParser.FIELD_NAME));
        } else ctx = null;

        return new PluralTranslation(plurals, ctx);
    }
}
