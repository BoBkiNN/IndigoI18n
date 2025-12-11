package xyz.bobkinn.indigoi18n.template;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import xyz.bobkinn.indigoi18n.template.format.StringTemplateFormatter;

import java.util.Arrays;
import java.util.stream.Stream;

public class TestStringTemplateFormatter {

    private final StringTemplateFormatter strF = new StringTemplateFormatter();

    private String formatStr(String text, Object... args) {
        var entry = TemplateParser.parse(text);
        return strF.format(null, entry, Arrays.asList(args));
    }

    static Stream<Arguments> provideStrFormatCases() {
        return Stream.of(
                Arguments.of("test %1!", new Object[]{1}, "test 1!"),
                Arguments.of("test %{1}!", new Object[]{1}, "test 1!"),
                Arguments.of("test %s!", new Object[]{1}, "test 1!"),
                Arguments.of("test %{s}!", new Object[]{1}, "test 1!"),

                Arguments.of("test %1!", new Object[]{"cat"}, "test cat!"),
                Arguments.of("test %{1}!", new Object[]{"cat"}, "test cat!"),
                Arguments.of("test %s!", new Object[]{"cat"}, "test cat!"),
                Arguments.of("test %{s}!", new Object[]{"cat"}, "test cat!"),

                Arguments.of("float is %{s:.2}", new Object[]{23.32}, "float is 23.32"),
                Arguments.of("double is %{s:.1}", new Object[]{23.3}, "double is 23.3"),

                Arguments.of("chance is %{s:%}", new Object[]{0.55}, "chance is 55.000000%"),
                Arguments.of("chance is %{s:#%}", new Object[]{0.55}, "chance is 55%"),
                Arguments.of("chance is %{s:.1%}", new Object[]{0.55321}, "chance is 55.3%"),

                Arguments.of("test %{s!h}", new Object[]{"zz"}, "test f40"),
                Arguments.of("test %{s!H}", new Object[]{"zz"}, "test F40"),
                Arguments.of("test %{s!h}", new Object[]{null}, "test null"),
                Arguments.of("test %{s!H}", new Object[]{null}, "test NULL"),

                Arguments.of("test %{s!s:.1}", new Object[]{255}, "test 2"),
                // x (hexadecimal) type is ignored here because !s converts number into string
                Arguments.of("test %{s!s:x}", new Object[]{255}, "test 255")
        );
    }

    @ParameterizedTest
    @MethodSource("provideStrFormatCases")
    void testStrFormatting(String format, Object[] args, String expected) {
        Assertions.assertEquals(expected, formatStr(format, args));
    }

    static Stream<Arguments> provideStrReprCases() {
        return Stream.of(
                Arguments.of("test %{s!r}", new Object[]{1}, "test 1"),
                Arguments.of("test %{s!r}", new Object[]{1.1}, "test 1.1"),
                Arguments.of("test %{s!r}!", new Object[]{"cat"}, "test 'cat'!"),
                Arguments.of("test %{s!r:.2}!", new Object[]{"cat"}, "test 'c!")
        );
    }

    @ParameterizedTest
    @MethodSource("provideStrReprCases")
    void testStrReprFormatting(String format, Object[] args, String expected) {
        Assertions.assertEquals(expected, formatStr(format, args));
    }
}
