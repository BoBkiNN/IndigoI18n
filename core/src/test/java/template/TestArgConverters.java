package template;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import xyz.bobkinn.indigoi18n.template.format.FormatSpec;
import xyz.bobkinn.indigoi18n.template.arg.ArgConverters;
import xyz.bobkinn.indigoi18n.template.Utils;

import java.util.stream.Stream;

public class TestArgConverters {
    /**
     * {@code _=+7}
     */
    @Test
    public void testAlignNumber() {
        var alg = new FormatSpec.Alignment(FormatSpec.AlignType.SIGN, '_');
        var v = ArgConverters.alignNumber(alg, 7, "+", "23");
        Assertions.assertEquals("+____23", v);
    }

    @Test
    public void testNumberFormat() {
        var f = Utils.formatIntGrouped(230_000, 10, 3, "_");
        Assertions.assertEquals("230_000", f);
    }

    private String formatInt(String spec, int number) {
        return ArgConverters.format(ArgConverters.INT_CONVERTER, spec, number);
    }

    static Stream<Arguments> provideIntFormats() {
        return Stream.of(
                Arguments.of("#x", 65232, "0xfed0"),
                Arguments.of("x", 65232, "fed0"),
                Arguments.of("#X", 65232, "0XFED0"),
                Arguments.of("X", 65232, "FED0"),
                Arguments.of("o", 65, "101"),
                Arguments.of("#o", 65, "0o101"),
                Arguments.of("b", 65, "1000001"),
                Arguments.of("#b", 65, "0b1000001"),
                Arguments.of("_=+#20o", 65, "+0o______________101"),
                Arguments.of("@^+10_", 16384, "@+16_384@@"),
                Arguments.of("@^+20_o", 16384, "@@@@@@+4_0000@@@@@@@"),
                Arguments.of("z>+20_o", 16384, "zzzzzzzzzzzzz+4_0000"),
                Arguments.of("z<+20_o", 16384, "+4_0000zzzzzzzzzzzzz"),
                Arguments.of("z=+20_o", 16384, "+zzzzzzzzzzzzz4_0000"),
                Arguments.of("_=10c", 65, "_________A"),
                Arguments.of("_>10c", 65, "_________A"),
                Arguments.of("_<10c", 65, "A_________"),
                Arguments.of("_^10c", 65, "____A_____"),
                Arguments.of("c", 2356456, "%{2356456:c}"),
                Arguments.of("10", 12, "        12")
        );
    }

    @ParameterizedTest
    @MethodSource("provideIntFormats")
    void testIntConverter(String format, int value, String expected) {
        Assertions.assertEquals(expected, formatInt(format, value));
    }

    private String formatStr(String spec, String text, boolean repr) {
        return ArgConverters.format(ArgConverters.STRING_CONVERTER, spec, repr, text);
    }

    static Stream<Arguments> provideStrFormats() {
        return Stream.of(
                Arguments.of("10", "a", false, "a         "),
                Arguments.of(">10", "a", false, "         a"),
                Arguments.of("^10", "a", false, "    a     "),
                Arguments.of(".3", "hello", false, "hel"),
                Arguments.of(".0", "hello", false, ""),
                Arguments.of(".30", "hello", false, "hello"),
                Arguments.of("10.3", "hello", false, "hel       "),
                Arguments.of(">10.3", "hello", false, "       hel"),
                Arguments.of("^10.3", "hello", false, "   hel    "),

                Arguments.of("10.3", "hello", true, "'he       "),
                Arguments.of(">10.3", "hello", true, "       'he"),
                Arguments.of("^10.3", "hello", true, "   'he    "),
                Arguments.of("_^10.3", "hello", true, "___'he____"),

                Arguments.of(".3", "hello", true, "'he")
        );
    }

    @ParameterizedTest
    @MethodSource("provideStrFormats")
    void testStrConverter(String format, String input, boolean quote, String expected) {
        Assertions.assertEquals(expected, formatStr(format, input, quote));
    }
}
