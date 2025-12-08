package xyz.bobkinn.indigoi18n.template;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import xyz.bobkinn.indigoi18n.template.format.StringTemplateFormatter;

import java.util.List;
import java.util.stream.Stream;

public class TestStringTemplateFormatter {

    private final StringTemplateFormatter strF = new StringTemplateFormatter();

    private String formatStr(String text, Object... args) {
        var entry = TemplateProcessor.parse(text);
        return strF.format(entry, List.of(args));
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
                Arguments.of("chance is %{s:.1%}", new Object[]{0.55321}, "chance is 55.3%")
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
