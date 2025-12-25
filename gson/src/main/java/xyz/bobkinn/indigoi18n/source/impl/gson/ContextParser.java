package xyz.bobkinn.indigoi18n.source.impl.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import org.jetbrains.annotations.NotNull;
import xyz.bobkinn.indigoi18n.context.Context;
import xyz.bobkinn.indigoi18n.context.ContextEntry;
import xyz.bobkinn.indigoi18n.context.impl.CountContext;

public class ContextParser {

    public static final String FIELD_NAME = "ctx";

    /**
     * Creates context entry based on key.<br>
     * Currently only {@link CountContext count context} is available because it is one actually needed and safe to override.
     * Other ones like LangKey, Source, Inline, I18n are mostly internal.
     */
    public ContextEntry createEntry(@NotNull String key, JsonElement value) {
        if (key.equals("count")) {
            return new CountContext(value.getAsInt());
        }
        return null;
    }

    public Context parse(@NotNull JsonElement el) throws JsonParseException {
        var obj = el.getAsJsonObject();
        var ctx = new Context();
        for (var e : obj.entrySet()) {
            var k = e.getKey();
            var c = createEntry(k, e.getValue());
            if (c != null) ctx.set(c);
        }
        return ctx;
    }
}
