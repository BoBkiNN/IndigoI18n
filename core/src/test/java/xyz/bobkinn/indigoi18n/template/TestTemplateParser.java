package xyz.bobkinn.indigoi18n.template;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import xyz.bobkinn.indigoi18n.template.arg.TemplateArgument;
import xyz.bobkinn.indigoi18n.template.format.FormatPattern;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class TestTemplateParser {

    @Test
    public void testAlignBoth() {
        var tr = new TemplateReader("<<");
        var al = FormatPattern.Alignment.read(tr);
        assertNotNull(al);
        assertEquals('<', al.fill());
        assertEquals(FormatPattern.AlignType.TO_LEFT, al.type());
    }

    @Test
    public void testAlignDef() {
        var tr = new TemplateReader("<");
        var al = FormatPattern.Alignment.read(tr);
        assertNotNull(al);
        assertNull(al.fill());
        assertEquals(FormatPattern.AlignType.TO_LEFT, al.type());
    }

    @Test
    public void testAlignChar() {
        var tr = new TemplateReader("0<");
        var al = FormatPattern.Alignment.read(tr);
        assertNotNull(al);
        assertEquals('0', al.fill());
        assertEquals(FormatPattern.AlignType.TO_LEFT, al.type());
    }

    @Test
    public void testAlignNone() {
        var tr = new TemplateReader("");
        var al = FormatPattern.Alignment.read(tr);
        assertNull(al);
    }

    @Test
    public void testAlignNone2() {
        var tr = new TemplateReader("d");
        var al = FormatPattern.Alignment.read(tr);
        assertNull(al);
    }

    @Test
    public void testFormatSpec() {
        var tr = new TemplateReader("_>10_.5E");
        var fs = FormatPattern.readFormatSpec(tr);
        System.out.println(fs);
        assertEquals(10, fs.getWidth());
        assertEquals(5, fs.getPrecision());
        assertEquals('E', fs.getType());
        assertEquals('_', fs.getIntPartGrouping());
    }

    @Test
    public void testFormatSource() {
        var tr = new TemplateReader(":_^10");
        var arg = TemplateParser.readArg(tr, 0);
        assertNotNull(arg.getPattern());
        assertEquals("_^10", arg.getPattern().getSource());
    }

    @ParameterizedTest
    @CsvSource({
            "'%s%s', 2",
            "'%s%s ', 3",
            "' %s%s', 3",
            "' %s%s ', 4",
            "' %s %s ', 5"
    })
    void testEntrySize(String template, int expectedSize) {
        assertEquals(expectedSize, TemplateParser.parse(template).parts().size());
    }

    @ParameterizedTest
    @CsvSource({
            "'%%s', '%s'",
            "'%%{s:.3e}', '%{s:.3e}'",
    })
    void testEscaping(String template, String text) {
        assertEquals(text, TemplateParser.parse(template).parts().get(0));
    }

    static Stream<Arguments> provideInlineArgs() {
        return Stream.of(
                Arguments.of("abc", new InlineTranslation("abc")),
                Arguments.of("abc:", new InlineTranslation("abc")),
                Arguments.of("abc::", new InlineTranslation("abc")),
                Arguments.of("abc::", new InlineTranslation("abc")),
                Arguments.of("abc:2:", new InlineTranslation("abc", 2, null)),
                Arguments.of("abc:26:", new InlineTranslation("abc", 26, null)),
                Arguments.of("abc:2:en", new InlineTranslation("abc", 2, "en")),
                Arguments.of("abc::en", new InlineTranslation("abc", 1, "en"))
        );
    }

    @ParameterizedTest
    @MethodSource("provideInlineArgs")
    void testInlineParse(String input, InlineTranslation expected) {
        Assertions.assertEquals(expected, TemplateParser.readInline(new TemplateReader(input)));
    }

    @Test
    void testInlines() {
        var entry = TemplateParser.parse("%s%{t:key}lol");
        assertInstanceOf(TemplateArgument.class, entry.part(0));
        assertEquals(new InlineTranslation("key"), entry.part(1));
        assertEquals("lol", entry.part(2));
        assertEquals(3, entry.parts().size());
    }
}
