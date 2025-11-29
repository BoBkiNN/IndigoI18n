package template;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import xyz.bobkinn.indigoi18n.template.StringTemplateFormatter;
import xyz.bobkinn.indigoi18n.template.TemplateProcessor;

import java.util.List;

public class TestStringTemplateFormatter {

    private final StringTemplateFormatter strF = new StringTemplateFormatter();

    private String formatStr(String text, Object... args) {
        var entry = TemplateProcessor.parse(text);
        return strF.format(entry, List.of(args));
    }

    @Test
    public void test() {
        Assertions.assertEquals("test 1!", formatStr("test %1!", 1));
        Assertions.assertEquals("test 1!", formatStr("test %{1}!", 1));
        Assertions.assertEquals("test 1!", formatStr("test %s!", 1));
        Assertions.assertEquals("test 1!", formatStr("test %{s}!", 1));

        Assertions.assertEquals("test cat!", formatStr("test %1!", "cat"));
        Assertions.assertEquals("test cat!", formatStr("test %{1}!", "cat"));
        Assertions.assertEquals("test cat!", formatStr("test %s!", "cat"));
        Assertions.assertEquals("test cat!", formatStr("test %{s}!", "cat"));

        Assertions.assertEquals("test 'cat'!", formatStr("test %{s!r}!", "cat"));
        Assertions.assertEquals("test 'c!", formatStr("test %{s!r:.2}!", "cat"));

        Assertions.assertEquals("float is 23.32", formatStr("float is %{s:.2}", 23.32));
        Assertions.assertEquals("double is 23.3", formatStr("double is %{s:.1}", 23.3));

        Assertions.assertEquals("chance is 55.000000%", formatStr("chance is %{s:%}", 0.55));
        Assertions.assertEquals("chance is 55%", formatStr("chance is %{s:#%}", 0.55));
        Assertions.assertEquals("chance is 55.3%", formatStr("chance is %{s:.1%}", 0.55321));
    }
}
