package template;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import xyz.bobkinn.indigoi18n.template.ArgConverters;

public class TestNumberConverter {

    private String formatNumber(String spec, Number number) {
        return ArgConverters.format(ArgConverters.NUMBER_CONVERTER, spec, number);
    }

    @Test
    public void testFloatFixed() {
        Assertions.assertEquals("123.456000", formatNumber("f", 123.456));
        Assertions.assertEquals("+123.456000", formatNumber("+f", 123.456));
        Assertions.assertEquals("123.45600", formatNumber(".5f", 123.456));
        Assertions.assertEquals("123.456000", formatNumber("0=9f", 123.456));
        Assertions.assertEquals("00123.456000", formatNumber("0=12f", 123.456));
        Assertions.assertEquals("123.456000", formatNumber(">f", 123.456));
        Assertions.assertEquals("123.456000   ", formatNumber("<13f", 123.456));
        Assertions.assertEquals("123", formatNumber("#f", 123.0));
    }

    @Test
    public void testFloatScientific() {
        Assertions.assertEquals("1.234560e+02", formatNumber("e", 123.456));
        Assertions.assertEquals("1.234560E+02", formatNumber("E", 123.456));
        Assertions.assertEquals("+1.234560e+02", formatNumber("+e", 123.456));
        Assertions.assertEquals("1.23456e+02", formatNumber(".5e", 123.456));
        Assertions.assertEquals("2.000000e+00", formatNumber("e", 2));
        Assertions.assertEquals("2e+00", formatNumber("#e", 2));
    }

    @Test
    public void testFloatPercent() {
        Assertions.assertEquals("12345.600000%", formatNumber("%", 123.456));
        Assertions.assertEquals("+12345.60%", formatNumber("+.2%", 123.456));
        Assertions.assertEquals("+12345%", formatNumber("+#.2%", 123.45));
    }

    @Test
    public void testFloatSpecialCases() {
        Assertions.assertEquals("nan", formatNumber("f", Double.NaN));
        Assertions.assertEquals("INF", formatNumber("F", Double.POSITIVE_INFINITY));
        Assertions.assertEquals("-inf", formatNumber("f", Double.NEGATIVE_INFINITY));
    }

    @Test
    public void testFloatGrouping() {
        Assertions.assertEquals("1_234.567000", formatNumber("_f", 1234.567));
        Assertions.assertEquals("+1_234.567000", formatNumber("+_f", 1234.567));
        Assertions.assertEquals("12_345.678900", formatNumber("_=12_f", 12345.6789));
    }

    @Test
    public void testComplex() {
        Assertions.assertEquals("+_12_345.679", formatNumber("_=+12_.3f", 12_345.678900));
    }
}
