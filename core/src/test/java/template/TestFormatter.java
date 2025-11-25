package template;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import xyz.bobkinn.indigoi18n.template.FormatSpec;
import xyz.bobkinn.indigoi18n.template.TemplateFormatter;

public class TestFormatter {
    /**
     * {@code _=+7}
     */
    @Test
    public void testAlignNumber() {
        var alg = new FormatSpec.Alignment(FormatSpec.AlignType.SIGN, '_');
        var v = TemplateFormatter.alignNumber(alg, 7, "+", "23");
        Assertions.assertEquals("+____23", v);
    }

    @Test
    public void testNumberFormat() {
        var f = TemplateFormatter.formatIntGrouped(230_000, 10, 3, "_");
        Assertions.assertEquals("230_000", f);
    }

    private String formatInt(String spec, int number) {
        var format = FormatSpec.parse(spec, false);
        return TemplateFormatter.INT_CONVERTER.format(number, format);
    }

    @Test
    public void testIntConverter() {
        Assertions.assertEquals("0xfed0", formatInt("#x", 65232));
        Assertions.assertEquals("fed0", formatInt("x", 65232));
        Assertions.assertEquals("0XFED0", formatInt("#X", 65232));
        Assertions.assertEquals("FED0", formatInt("X", 65232));
        Assertions.assertEquals("101", formatInt("o", 65));
        Assertions.assertEquals("0o101", formatInt("#o", 65));
        Assertions.assertEquals("1000001", formatInt("b", 65));
        Assertions.assertEquals("0b1000001", formatInt("#b", 65));
        Assertions.assertEquals("+0o______________101", formatInt("_=+#20o", 65));
        Assertions.assertEquals("@+16_384@@", formatInt("@^+10_", 16384));
        Assertions.assertEquals("@@@@@@+4_0000@@@@@@@", formatInt("@^+20_o", 16384));
        Assertions.assertEquals("zzzzzzzzzzzzz+4_0000", formatInt("z>+20_o", 16384));
        Assertions.assertEquals("+4_0000zzzzzzzzzzzzz", formatInt("z<+20_o", 16384));
        Assertions.assertEquals("+zzzzzzzzzzzzz4_0000", formatInt("z=+20_o", 16384));
        Assertions.assertEquals("_________A", formatInt("_=10c", 65));
        Assertions.assertEquals("_________A", formatInt("_>10c", 65));
        Assertions.assertEquals("A_________", formatInt("_<10c", 65));
        Assertions.assertEquals("____A_____", formatInt("_^10c", 65));
        Assertions.assertEquals("%{2356456:c}", formatInt("c", 2356456));
    }
}
