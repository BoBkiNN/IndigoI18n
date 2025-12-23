package xyz.bobkinn.indigoi18n.template;

import org.junit.jupiter.api.Test;
import xyz.bobkinn.indigoi18n.Indigo;
import xyz.bobkinn.indigoi18n.template.arg.ArgConverters;
import xyz.bobkinn.indigoi18n.template.arg.ArgumentConverter;
import xyz.bobkinn.indigoi18n.template.format.FormatPattern;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

class BigNumberConvertersTest {

    // shortcut
    private static <T> String fmt(ArgumentConverter<T, String> conv, String format, T value) {
        return ArgConverters.format(conv, format, value);
    }

    /* =========================
     * BigInteger
     * ========================= */

    @Test
    void bigint_decimal() {
        assertEquals("123456", fmt(ArgConverters.BIG_INT_CONVERTER, "d", new BigInteger("123456")));
        assertEquals("-42", fmt(ArgConverters.BIG_INT_CONVERTER, "d", new BigInteger("-42")));
    }

    @Test
    void bigint_hex() {
        assertEquals("ff", fmt(ArgConverters.BIG_INT_CONVERTER, "x", new BigInteger("255")));
        assertEquals("FF", fmt(ArgConverters.BIG_INT_CONVERTER, "X", new BigInteger("255")));
    }

    @Test
    void bigint_hex_special() {
        assertEquals("0xff", fmt(ArgConverters.BIG_INT_CONVERTER, "#x", new BigInteger("255")));
        assertEquals("0XFF", fmt(ArgConverters.BIG_INT_CONVERTER, "#X", new BigInteger("255")));
    }

    @Test
    void bigint_binary_and_octal() {
        assertEquals("0b1010", fmt(ArgConverters.BIG_INT_CONVERTER, "#b", new BigInteger("10")));
        assertEquals("0o12", fmt(ArgConverters.BIG_INT_CONVERTER, "#o", new BigInteger("10")));
    }

    @Test
    void bigint_grouping() {
        assertEquals("1_234_567",
                fmt(ArgConverters.BIG_INT_CONVERTER, "_d", new BigInteger("1234567")));
    }

    @Test
    void bigint_sign() {
        assertEquals("+42", fmt(ArgConverters.BIG_INT_CONVERTER, "+d", new BigInteger("42")));
        assertEquals("-42", fmt(ArgConverters.BIG_INT_CONVERTER, "+d", new BigInteger("-42")));
    }

    /* =========================
     * BigDecimal
     * ========================= */

    @Test
    void big_decimal_plain() {
        assertEquals("123.450000",
                fmt(ArgConverters.BIG_DECIMAL_CONVERTER, "f", new BigDecimal("123.45")));
    }

    @Test
    void big_decimal_precision() {
        assertEquals("123.457",
                fmt(ArgConverters.BIG_DECIMAL_CONVERTER, ".3f", new BigDecimal("123.45678")));
    }

    @Test
    void big_decimal_scientific() {
        String out = fmt(
                ArgConverters.BIG_DECIMAL_CONVERTER,
                ".3e",
                new BigDecimal("12345.6")
        );
        assertTrue(out.contains("e") || out.contains("E"));
    }

    @Test
    void big_decimal_percent() {
        assertEquals("12.30%",
                fmt(ArgConverters.BIG_DECIMAL_CONVERTER, ".2%", new BigDecimal("0.123")));
    }

    @Test
    void big_decimal_grouping() {
        assertEquals("1,234,567.890000",
                fmt(ArgConverters.BIG_DECIMAL_CONVERTER, ",f", new BigDecimal("1234567.89")));
    }

    @Test
    void big_decimal_hash_removes_fraction_if_zero() {
        assertEquals("42",
                fmt(ArgConverters.BIG_DECIMAL_CONVERTER, "#f", new BigDecimal("42.000")));
    }

    @Test
    void big_decimal_sign() {
        assertEquals("+1.50",
                fmt(ArgConverters.BIG_DECIMAL_CONVERTER, "+.2f", new BigDecimal("1.5")));
        assertEquals("-1.50",
                fmt(ArgConverters.BIG_DECIMAL_CONVERTER, "+.2f", new BigDecimal("-1.5")));
    }

    // n

    @SuppressWarnings("SameParameterValue")
    private static String formatLangBigInt(String lang, String format, BigInteger number) {
        var ctx = Indigo.INSTANCE.newContext(lang, "test");
        var f = FormatPattern.parse(format);
        return ArgConverters.BIG_INT_CONVERTER.format(ctx, number, f);
    }

    @Test
    void testNModePositive() {
        // ru_RU — space as thousands separator
        assertEquals("+.12 500 000",
                formatLangBigInt("ru_ru", ".=+12n", new BigInteger("12500000")));

        // de_DE — dot as thousands separator
        assertEquals("+.12.500.000",
                formatLangBigInt("de_de", ".=+12n", new BigInteger("12500000")));
    }

    @Test
    void testNModeNegative() {
        // ru_RU negative number
        assertEquals("-.12 500 000",
                formatLangBigInt("ru_ru", ".=+12n", new BigInteger("-12500000")));

        // de_DE negative number
        assertEquals("-.12.500.000",
                formatLangBigInt("de_de", ".=+12n", new BigInteger("-12500000")));
    }

    @SuppressWarnings("SameParameterValue")
    private static String formatLangBigDecimal(String lang, String format, BigDecimal number) {
        var ctx = Indigo.INSTANCE.newContext(lang, "test");
        var f = FormatPattern.parse(format);
        return ArgConverters.BIG_DECIMAL_CONVERTER.format(ctx, number, f);
    }

    @Test
    void testNModePositiveDecimal() {
        BigDecimal val = new BigDecimal("12500000.12");

        // ru_RU — non-breaking space, comma decimal
        assertEquals("+.12 500 000,12",
                formatLangBigDecimal("ru_ru", ".=+15n", val));

        // de_DE — dot grouping, comma decimal
        assertEquals("+.12.500.000,12",
                formatLangBigDecimal("de_de", ".=+15n", val));
    }

    @Test
    void testNModeNegativeDecimal() {
        BigDecimal val = new BigDecimal("-12500000.12");

        // ru_RU negative
        assertEquals("-.12 500 000,12",
                formatLangBigDecimal("ru_ru", ".=+15n", val));

        // de_DE negative
        assertEquals("-.12.500.000,12",
                formatLangBigDecimal("de_de", ".=+15n", val));
    }

    @Test
    void testNModeZeroTrimSpecial() {
        // value with .00 fractional part, '#' (special) should trim zeros
        BigDecimal val = new BigDecimal("12500000.00");

        // ru_RU locale, `,00` is trimmed
        assertEquals("+....12 500 000",
                formatLangBigDecimal("ru_ru", ".=+15n#", val));

        // de_DE locale, `,00` is trimmed
        assertEquals("+....12.500.000",
                formatLangBigDecimal("de_de", ".=+15n#", val));
    }
}
