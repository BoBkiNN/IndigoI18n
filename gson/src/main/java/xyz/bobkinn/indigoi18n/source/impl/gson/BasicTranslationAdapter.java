package xyz.bobkinn.indigoi18n.source.impl.gson;

import com.google.gson.*;
import lombok.RequiredArgsConstructor;
import xyz.bobkinn.indigoi18n.context.Context;
import xyz.bobkinn.indigoi18n.data.BasicTranslation;

import java.lang.reflect.Type;

/**
 * Used to ignore key case
 */
@RequiredArgsConstructor
public class BasicTranslationAdapter implements JsonDeserializer<BasicTranslation> {
    private final ContextParser contextParser;

    @Override
    public BasicTranslation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();

        var el = obj.get("text");
        if (el == null) {
            throw new JsonParseException("No text field provided");
        }
        var text = el.getAsString();

        Context ctx;
        if (obj.has(ContextParser.FIELD_NAME)) {
            ctx = contextParser.parse(obj.getAsJsonObject(ContextParser.FIELD_NAME));
        } else ctx = null;

        return new BasicTranslation(text, ctx);
    }
}
