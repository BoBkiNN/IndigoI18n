package xyz.bobkinn.indigoi18n.source.impl;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;
import xyz.bobkinn.indigoi18n.data.DefaultTranslation;
import xyz.bobkinn.indigoi18n.data.Translation;
import xyz.bobkinn.indigoi18n.source.ISourceTextAdder;

import java.io.File;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GsonTranslationSourceTest {

    static class TestAdder implements ISourceTextAdder {
        // TODO store Translation here instead of forcing DefaultTranslation in add()
        Map<String, String> data = new HashMap<>();

        @Override
        public void add(String key, String language, Translation value) {
            data.put(key, ((DefaultTranslation) value).getText());
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

        assertEquals(2, adder.data.size());
        assertEquals("Hello", adder.data.get("hello"));
        assertEquals("Goodbye", adder.data.get("bye"));
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
        assertEquals("value1", adder.data.get("key1"));
        assertEquals("value2", adder.data.get("key2"));
    }
}
