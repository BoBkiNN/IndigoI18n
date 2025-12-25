package xyz.bobkinn.indigoi18n.source.impl.gson;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import xyz.bobkinn.indigoi18n.StringI18n;
import xyz.bobkinn.indigoi18n.context.impl.CountContext;
import xyz.bobkinn.indigoi18n.data.BasicTranslation;
import xyz.bobkinn.indigoi18n.data.PluralTranslation;
import xyz.bobkinn.indigoi18n.data.Translation;
import xyz.bobkinn.indigoi18n.source.ISourceTextAdder;

import java.io.File;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GsonTranslationSourceTest {

    static class TestAdder implements ISourceTextAdder {
        Map<String, Translation> data = new HashMap<>();

        @Override
        public void add(String key, String language, Translation value) {
            data.put(key, value);
        }

        public String basic(String key) {
            return ((BasicTranslation) data.get(key)).getText();
        }

        public String plural(String key, String language, int count) {
            var ctx = new StringI18n().newContext(language, key);
            ctx.set(new CountContext(count));
            var t = (PluralTranslation) data.get(key);
            var ovr = t.getContextOverride();
            if (ovr != null) ctx.merge(ovr, true);
            return t.resolve(ctx);
        }
    }

    @Test
    void testLoadFromFile() {
        TestAdder adder = loadFromResource("en.json", "en");

        assertEquals(3, adder.data.size());
        assertEquals("Hello", adder.basic("hello"));
        assertEquals("Goodbye", adder.basic("bye"));
        assertEquals("1", adder.plural("pl", "ru", 1));
        assertEquals("25", adder.plural("pl", "ru", 25));
        assertEquals("3", adder.plural("pl", "ru", 3));
    }

    @Test
    void testLoadFromElement() {
        var json = new JsonObject();
        json.addProperty("key1", "value1");
        json.addProperty("key2", "value2");

        GsonTranslationSource source = GsonTranslationSource.fromElement(null, "en", json);

        TestAdder adder = new TestAdder();
        source.load(adder);

        assertEquals(2, adder.data.size());
        assertEquals("value1", adder.basic("key1"));
        assertEquals("value2", adder.basic("key2"));
    }

    @SuppressWarnings("SameParameterValue")
    private @NotNull TestAdder loadFromResource(String name, String langName) {
        var resource = getClass().getClassLoader().getResource(name);
        assertNotNull(resource, "Test resource not found");

        File file;
        try {
            file = new File(resource.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        // Create source from file
        GsonTranslationSource source = GsonTranslationSource.fromFile(file.toURI(), langName, file);

        TestAdder adder = new TestAdder();
        source.load(adder);
        return adder;
    }

    @Test
    void testContextOverride() {
        var r = loadFromResource("ovr.json", "en");
        assertEquals("1", r.plural("always_1", "ru", 1));
        assertEquals("1", r.plural("always_1", "ru", 25));
        assertEquals("1", r.plural("always_1", "ru", 3));
        assertEquals("1", r.plural("always_1", "ru", 5));
    }
}
