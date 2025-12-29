package xyz.bobkinn.indigoi18n.format.adventure;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import xyz.bobkinn.indigoi18n.context.Context;
import xyz.bobkinn.indigoi18n.context.impl.LangKeyContext;
import xyz.bobkinn.indigoi18n.data.TemplateCache;
import xyz.bobkinn.indigoi18n.template.TemplateParser;
import xyz.bobkinn.indigoi18n.template.format.StringTemplateFormatter;

import java.util.List;
import java.util.Locale;

class TestComponentI18nFormat {

    private final TemplateCache cache = new TemplateCache(TemplateParser.INSTANCE);
    private final ComponentTemplateFormatter componentFormatter =
            new ComponentTemplateFormatter(new StringTemplateFormatter(), Component::text);

    private final ComponentI18nFormat format = new ComponentI18nFormat(cache, componentFormatter) {
        @Override
        public Component deserialize(String text) {
            return Component.text(text);
        }
    };

    @SuppressWarnings("SameParameterValue")
    private Context ctxWithInfo(String key) {
        var ctx = new Context();
        ctx.set(new LangKeyContext("en", key, Locale.ENGLISH));
        return ctx;
    }

    private static Component text(String s) {
        return Component.text(s);
    }

    @Test
    void replacesSimpleArgument() {
        Context ctx = ctxWithInfo("test");

        Component input = Component.text("hello %{s}");
        Component result = format.replaceArguments(ctx, input, List.of("world"));

        Assertions.assertEquals(
                text("")
                        .append(text("hello "), text("world")),
                result
        );
    }

    @Test
    void appliesFormattingToArgument() {
        Context ctx = ctxWithInfo("test");

        Component input = Component.text("value %{:.2f}");
        Component result = format.replaceArguments(ctx, input, List.of(1.234));

        Assertions.assertEquals(
                text("").append(text("value "), text("1.23")),
                result
        );
    }

    @Test
    void unknownArgumentIndexIsPreserved() {
        Context ctx = ctxWithInfo("test");

        Component input = Component.text("value %2");
        Component result = format.replaceArguments(ctx, input, List.of(10));

        Assertions.assertEquals(
                Component.empty().append(text("value "), text("%2")),
                result
        );
    }

    @Test
    void processesNestedTextComponents() {
        Context ctx = ctxWithInfo("test");

        Component input = Component.empty()
                .append(Component.text("a %{s}"))
                .append(Component.text(" b %{s}"));

        Component result = format.replaceArguments(ctx, input, List.of("X", "Y"));

        Assertions.assertEquals(
                Component.text("") // outer root
                        .append(
                                Component.text("") // first child root
                                        .append(Component.text("a "))
                                        .append(Component.text("X"))
                        )
                        .append(
                                Component.text("") // second child root
                                        .append(Component.text(" b "))
                                        .append(Component.text("Y"))
                        ),
                result
        );
    }


    @Test
    void preservesOriginalStyles() {
        Context ctx = ctxWithInfo("test");

        TextComponent input = Component.text("hello %{s}")
                .color(NamedTextColor.RED);

        Component result = format.replaceArguments(ctx, input, List.of("world"));

        Assertions.assertEquals(
                Component.empty().append(text("hello "), text("world"))
                        .color(NamedTextColor.RED),
                result
        );
    }

    @Test
    void testLegacyConv() {
        var f = new ComponentI18nFormat(cache, new ComponentTemplateFormatter(new StringTemplateFormatter(),
                s -> Component.text(s).color(NamedTextColor.RED))) {
            @Override
            public Component deserialize(String text) {
                return Component.text(text);
            }
        };
        var ctx = ctxWithInfo("test2");
        var r = f.format(ctx, "%s", List.of("red"));
        Assertions.assertEquals(Component.empty()
                .append(Component.text("red")
                        .color(NamedTextColor.RED)), r);
    }

}
