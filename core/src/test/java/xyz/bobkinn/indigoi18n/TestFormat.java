package xyz.bobkinn.indigoi18n;

import org.junit.jupiter.api.Test;
import xyz.bobkinn.indigoi18n.source.TranslationSource;
import xyz.bobkinn.indigoi18n.source.impl.PropertiesSource;

import java.net.URI;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class TestFormat {

    Properties props = new Properties();
    TranslationSource source = new PropertiesSource(URI.create("file://en.properties"), "en", props);

    {
        props.put("test", "Test %s %s");
        props.put("test2", "Test %s %s %s");
    }

    @Test
    public void testArgs() {
        var i18n = new StringI18n();
        i18n.load(source);
        assertEquals("Test %1 %2", i18n.parse("en", "test"));
        assertEquals("Test 23 %2", i18n.parse("en", "test", 23));
        assertEquals("Test 23 24", i18n.parse("en", "test", 23, 24));
        assertEquals("Test 23 24", i18n.parse("en", "test", 23, 24, 25));
        assertEquals("Test null 24", i18n.parse("en", "test", null, 24));
    }
}
