package xyz.bobkinn.indigoi18n;

import org.junit.jupiter.api.Test;
import xyz.bobkinn.indigoi18n.context.Context;
import xyz.bobkinn.indigoi18n.context.impl.InlineContext;
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
        props.put("test2", "Test %1 %1 %2");
        props.put("it.i", "cat");
        props.put("it", "inlined value is: %{t:it.i}");
        props.put("it2.i", "cat %1");
        props.put("it2", "inlined value %1 is: %{t:it2.i}");

        props.put("it3", "[%{t:it3}"); // inline self with depth 1
        props.put("it4", "[%{t:it4:0}"); // inline self with no depth
        props.put("it5", "[%{t:it5:5}"); // inline self with depth 5
        props.put("it6", "%{t:target}");
    }

    @Test
    public void testArgs() {
        var i18n = new StringI18n();
        i18n.setup();
        i18n.load(source);
        assertEquals("Test %1 %2", i18n.parse("en", "test"));
        assertEquals("Test 23 %2", i18n.parse("en", "test", 23));
        assertEquals("Test 23 24", i18n.parse("en", "test", 23, 24));
        assertEquals("Test 23 24", i18n.parse("en", "test", 23, 24, 25));
        assertEquals("Test null 24", i18n.parse("en", "test", null, 24));
        assertEquals("Test 23 23 24", i18n.parse("en", "test2", 23, 24));
    }

    @Test
    public void testInline() {
        var i18n = StringI18n.create();
        i18n.load(source);
        assertEquals("inlined value is: cat", i18n.parse("en", "it"));

        assertEquals("inlined value 12 is: cat 12", i18n.parse("en", "it2", 12));

        assertEquals("[[<it3>", i18n.parse("en", "it3"));
        assertEquals("[<it4>", i18n.parse("en", "it4"));
        assertEquals("[[[[[[<it5>", i18n.parse("en", "it5"));
        var wc = new Context().with(new InlineContext(0)); // remain no depth
        assertEquals("<target>", i18n.parse(wc, "en", "it6"));
    }
}
