package xyz.bobkinn.indigoi18n.source.impl;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;
import xyz.bobkinn.indigoi18n.StringI18n;
import xyz.bobkinn.indigoi18n.context.impl.CountContext;
import xyz.bobkinn.indigoi18n.data.DefaultTranslation;
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

        public String plain(String key) {
            return ((DefaultTranslation) data.get(key)).getText();
        }

        public String plural(String key, String language, int count) {
            var ctx = new StringI18n().newContext(language, key);
            ctx.set(new CountContext(count));
            var t = (PluralTranslation) data.get(key);
            return t.resolve(ctx);
        }
    }

    @Test
    void testLoadFromFile() throws URISyntaxException {
        // Load file from test resources
        var resource = getClass().getClassLoader().getResource("en.json");
        assertNotNull(resource, "Test resource not found");

        File file = new File(resource.toURI());

        // Create source from file
        GsonTranslationSource source = GsonTranslationSource.fromFile(file.toURI(), "en", file);

        TestAdder adder = new TestAdder();
        source.load(adder);

        assertEquals(3, adder.data.size());
        assertEquals("Hello", adder.plain("hello"));
        assertEquals("Goodbye", adder.plain("bye"));
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
        assertEquals("value1", adder.plain("key1"));
        assertEquals("value2", adder.plain("key2"));
    }
}
