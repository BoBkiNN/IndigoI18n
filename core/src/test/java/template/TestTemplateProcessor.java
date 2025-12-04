package template;

import org.junit.jupiter.api.Test;
import xyz.bobkinn.indigoi18n.template.FormatSpec;
import xyz.bobkinn.indigoi18n.template.TemplateProcessor;
import xyz.bobkinn.indigoi18n.template.TemplateReader;

import static org.junit.jupiter.api.Assertions.*;

public class TestTemplateProcessor {
    @Test
    public void test1() {
        final int[] pn = {0};
        final int[] an = {0};
        TemplateProcessor.parse("hello %s", s -> {
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
        var al = FormatSpec.Alignment.read(tr);
        assertNotNull(al);
        assertEquals('<', al.fill());
        assertEquals(FormatSpec.AlignType.TO_LEFT, al.type());
    }

    @Test
    public void testAlignDef() {
        var tr = new TemplateReader("<");
        var al = FormatSpec.Alignment.read(tr);
        assertNotNull(al);
        assertNull(al.fill());
        assertEquals(FormatSpec.AlignType.TO_LEFT, al.type());
    }

    @Test
    public void testAlignChar() {
        var tr = new TemplateReader("0<");
        var al = FormatSpec.Alignment.read(tr);
        assertNotNull(al);
        assertEquals('0', al.fill());
        assertEquals(FormatSpec.AlignType.TO_LEFT, al.type());
    }

    @Test
    public void testAlignNone() {
        var tr = new TemplateReader("");
        var al = FormatSpec.Alignment.read(tr);
        assertNull(al);
    }

    @Test
    public void testAlignNone2() {
        var tr = new TemplateReader("d");
        var al = FormatSpec.Alignment.read(tr);
        assertNull(al);
    }

    @Test
    public void testFormatSpec() {
        var tr = new TemplateReader("_>10_.5E");
        var fs = FormatSpec.readFormatSpec(tr, false);
        System.out.println(fs);
        assertEquals(10, fs.getWidth());
        assertEquals(5, fs.getPrecision());
        assertEquals('E', fs.getType());
        assertEquals('_', fs.getIntPartGrouping());
    }

    @Test
    public void testFormatSource() {
        var tr = new TemplateReader("{:_^10}");
        var arg = TemplateProcessor.readArg(tr, 0);
        assertNotNull(arg.getFormatSpec());
        assertEquals("_^10", arg.getFormatSpec().getSource());
    }

    @Test
    public void testEntrySize() {
        assertEquals(2, TemplateProcessor.parse("%s%s").parts().size());
        assertEquals(3, TemplateProcessor.parse("%s%s ").parts().size());
        assertEquals(3, TemplateProcessor.parse(" %s%s").parts().size());
        assertEquals(4, TemplateProcessor.parse(" %s%s ").parts().size());
        assertEquals(5, TemplateProcessor.parse(" %s %s ").parts().size());
    }
}
