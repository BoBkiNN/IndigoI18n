package xyz.bobkinn.indigoi18n;

import org.junit.jupiter.api.Test;
import xyz.bobkinn.indigoi18n.source.TranslationSource;
import xyz.bobkinn.indigoi18n.source.impl.PropertiesSource;

import java.net.URI;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class TestSources {

    Properties props = new Properties();
    TranslationSource source = new PropertiesSource(URI.create("file://en.properties"), "en", props);

    {
        props.put("test", "Test");
        props.put("test2", "Test 2");
    }

    @Test
    public void testSources() {
        var i18n = StringI18n.create();
        i18n.load(source);
        assertEquals("Test", i18n.parse("en", "test"));
        assertEquals("Test 2", i18n.parse("en", "test2"));
        assertEquals("test3", i18n.parse("en", "test3"));
        assertEquals("test2", i18n.parse("ru", "test2"));
        assertEquals(source, i18n.texts.sourcesWith("test2").stream().findFirst().orElseThrow());
        assertTrue(i18n.texts.sourcesWith("test2").contains(source));
        assertTrue(i18n.texts.sourcesWith("test").contains(source));
        assertFalse(i18n.texts.sourcesWith("test3").contains(source));
        assertTrue(i18n.texts.sourcesWith("test", "en").contains(source));
        assertTrue(i18n.texts.sourcesWith("test2", "en").contains(source));

        assertFalse(i18n.texts.sourcesWith("test2", "ru").contains(source));
        assertFalse(i18n.texts.sourcesWith("test3", "ru").contains(source));
        assertFalse(i18n.texts.sourcesWith("test3", "en").contains(source));
    }

    @Test
    public void testUnload() {
        var i18n = StringI18n.create();
        i18n.load(source);
        assertEquals("Test 2", i18n.parse("en", "test2"));
        i18n.unload(source);
        assertEquals("test2", i18n.parse("en", "test2"));
    }
}
