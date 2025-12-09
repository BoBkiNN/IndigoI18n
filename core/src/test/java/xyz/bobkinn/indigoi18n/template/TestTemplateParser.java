package xyz.bobkinn.indigoi18n.template;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import xyz.bobkinn.indigoi18n.template.format.FormatPattern;

import static org.junit.jupiter.api.Assertions.*;

public class TestTemplateParser {
    @Test
    public void test1() {
        final int[] pn = {0};
        final int[] an = {0};
        TemplateParser.parse("hello %s", s -> {
            pn[0]++;
            assertEquals("hello ", s);
        }, a -> {
            an[0]++;
            assertEquals(0, a.getIndex());
        });
        assertEquals(1, pn[0]);
        assertEquals(1, an[0]);
    }

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
        var tr = new TemplateReader("{:_^10}");
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
}
