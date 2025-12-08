package xyz.bobkinn.indigoi18n.template;

import org.junit.jupiter.api.Test;
import xyz.bobkinn.indigoi18n.template.TemplateReader;

import static org.junit.jupiter.api.Assertions.*;

public class TestTemplateReader {
    @Test
    public void testPeek() {
        var tr = new TemplateReader("texo");
        assertEquals('t', tr.peek());
        assertEquals('o', tr.peek(3));
        assertEquals('t', tr.next());
        assertEquals('e', tr.peek());
        assertEquals('o', tr.peek(2));
    }

    @Test
    public void testInt() {
        var tr = new TemplateReader("123");
        assertEquals(123, tr.readUnsignedNumber());
    }

    @Test
    public void testMark() {
        var tr = new TemplateReader("abcdef");
        tr.skip();
        tr.mark();
        tr.skip();
        tr.skip();
        assertEquals("bc", tr.markedPart());
        tr.reset();
        assertEquals('b', tr.next());
    }

}
