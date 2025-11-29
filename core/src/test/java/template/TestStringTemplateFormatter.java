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
    }
}
