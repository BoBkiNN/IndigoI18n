package xyz.bobkinn.indigoi18n.render.adventure;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import xyz.bobkinn.indigoi18n.context.Context;
import xyz.bobkinn.indigoi18n.template.TemplateParser;
import xyz.bobkinn.indigoi18n.template.arg.TemplateArgument;
import xyz.bobkinn.indigoi18n.template.format.StringTemplateFormatter;

class TestComponentTemplateFormatter {

    private final StringTemplateFormatter stringFormatter = new StringTemplateFormatter();
    private final ComponentTemplateFormatter componentFormatter =
            new ComponentTemplateFormatter(stringFormatter, Component::text);

    private Component formatArg(String pattern, Object value) {
        var entry = TemplateParser.INSTANCE.parse("%{" + pattern + "}");
        TemplateArgument arg = entry.argumentAt(0);

        return componentFormatter.formatArgument(
                new Context(),
                arg,
                value
        );
    }

    @Test
    void formatsViaStringFormatterFallback() {
        Component c = formatArg(":.2f", 12.345);

        Assertions.assertEquals(
                "12.35",
                ((TextComponent) c).content()
        );
    }

    @Test
    void textComponentIsFormattedNotPassedAsIs() {
        TextComponent input = Component.text("cat");

        Component c = formatArg(":>5", input);

        Assertions.assertEquals(
                "  cat",
                ((TextComponent) c).content()
        );
    }

    @Test
    void componentArgumentPassesThroughUnchanged() {
        Component input = Component.text("raw");

        Component c = formatArg("", input);

        Assertions.assertEquals(input, c);
    }

    @Test
    void rawRepresentationIsUsed() {
        Component c = formatArg("!r", "cat");

        Assertions.assertEquals(
                "'cat'",
                ((TextComponent) c).content()
        );
    }

    @Test
    void stringRepresentationConvertsNonString() {
        Component c = formatArg("!s:.1", 255);

        Assertions.assertEquals(
                "2",
                ((TextComponent) c).content()
        );
    }

    @Test
    void hexRepresentationLowercase() {
        Component c = formatArg("!h", 255);

        Assertions.assertEquals(
                "ff",
                ((TextComponent) c).content()
        );
    }

    @Test
    void hexRepresentationUppercase() {
        Component c = formatArg("!H", 255);

        Assertions.assertEquals(
                "FF",
                ((TextComponent) c).content()
        );
    }

    @Test
    void nullIsFormattedNotIgnored() {
        Component c = formatArg("", null);

        Assertions.assertEquals(
                "null",
                ((TextComponent) c).content()
        );
    }

    @Test
    void argumentIsInsertedInCorrectPlace() {
        var entry = TemplateParser.INSTANCE.parse("a %{!s} b");
        var arg = entry.argumentAt(1);

        Component mid = componentFormatter.formatArgument(
                new Context(),
                arg,
                10
        );

        Assertions.assertEquals(
                "10",
                ((TextComponent) mid).content()
        );
    }
}
