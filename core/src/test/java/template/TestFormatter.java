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
        Assertions.assertEquals("        12", formatInt("10", 12));
    }

    private String formatStr(String spec, String text, boolean repr) {
        var format = FormatSpec.parse(spec, repr);
        return TemplateFormatter.STRING_CONVERTER.format(text, format);
    }

    private String formatStr(String spec, String text) {
        return formatStr(spec, text, false);
    }

    @Test
    public void testStrConverter() {
        Assertions.assertEquals("a         ", formatStr("10", "a"));
        Assertions.assertEquals("         a", formatStr(">10", "a"));
        Assertions.assertEquals("    a     ", formatStr("^10", "a"));
        Assertions.assertEquals("hel", formatStr(".3", "hello"));
        Assertions.assertEquals("", formatStr(".0", "hello"));
        Assertions.assertEquals("hello", formatStr(".30", "hello"));
        Assertions.assertEquals("hel       ", formatStr("10.3", "hello"));
        Assertions.assertEquals("       hel", formatStr(">10.3", "hello"));
        Assertions.assertEquals("   hel    ", formatStr("^10.3", "hello"));

        Assertions.assertEquals("'he       ", formatStr("10.3", "hello", true));
        Assertions.assertEquals("       'he", formatStr(">10.3", "hello", true));
        Assertions.assertEquals("   'he    ", formatStr("^10.3", "hello", true));

        Assertions.assertEquals("___'he____", formatStr("_^10.3", "hello", true));

        Assertions.assertEquals("'he", formatStr(".3", "hello", true));
    }
}
