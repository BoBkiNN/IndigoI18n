package template;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import xyz.bobkinn.indigoi18n.template.arg.ArgConverters;

import java.util.stream.Stream;

public class TestNumberConverter {

    private String formatNumber(String spec, Number number) {
        return ArgConverters.format(ArgConverters.NUMBER_CONVERTER, spec, number);
    }

    static Stream<Arguments> provideFloatFormats() {
        return Stream.of(
                // Fixed-point
                Arguments.of("f", 123.456, "123.456000"),
                Arguments.of("+f", 123.456, "+123.456000"),
                Arguments.of(".5f", 123.456, "123.45600"),
                Arguments.of("0=9f", 123.456, "123.456000"),
                Arguments.of("0=12f", 123.456, "00123.456000"),
                Arguments.of(">f", 123.456, "123.456000"),
                Arguments.of("<13f", 123.456, "123.456000   "),
                Arguments.of("#f", 123.0, "123"),

                // Scientific
                Arguments.of("e", 123.456, "1.234560e+02"),
                Arguments.of("E", 123.456, "1.234560E+02"),
                Arguments.of("+e", 123.456, "+1.234560e+02"),
                Arguments.of(".5e", 123.456, "1.23456e+02"),
                Arguments.of("e", 2, "2.000000e+00"),
                Arguments.of("#e", 2, "2e+00"),

                // Percent
                Arguments.of("%", 123.456, "12345.600000%"),
                Arguments.of("+.2%", 123.456, "+12345.60%"),
                Arguments.of("+#.2%", 123.45, "+12345%"),

                // Special cases
                Arguments.of("f", Double.NaN, "nan"),
                Arguments.of("F", Double.POSITIVE_INFINITY, "INF"),
                Arguments.of("f", Double.NEGATIVE_INFINITY, "-inf"),

                // Grouping
                Arguments.of("_f", 1234.567, "1_234.567000"),
                Arguments.of("+_f", 1234.567, "+1_234.567000"),
                Arguments.of("_=12_f", 12345.6789, "12_345.678900"),

                Arguments.of("_=+12_.3f", 12_345.678900, "+_12_345.679")
        );
    }

    @ParameterizedTest
    @MethodSource("provideFloatFormats")
    void testFloatNumberFormatting(String format, Number value, String expected) {
        Assertions.assertEquals(expected, formatNumber(format, value));
    }

}
