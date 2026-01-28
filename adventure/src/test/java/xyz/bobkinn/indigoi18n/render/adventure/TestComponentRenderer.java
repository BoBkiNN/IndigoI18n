package xyz.bobkinn.indigoi18n.render.adventure;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import xyz.bobkinn.indigoi18n.context.Context;
import xyz.bobkinn.indigoi18n.context.impl.LangKeyContext;
import xyz.bobkinn.indigoi18n.data.TemplateCache;
import xyz.bobkinn.indigoi18n.render.adventure.format.ComponentRenderer;
import xyz.bobkinn.indigoi18n.render.adventure.format.LegacyComponentRenderer;
import xyz.bobkinn.indigoi18n.render.adventure.format.MiniMessageComponentRenderer;
import xyz.bobkinn.indigoi18n.template.TemplateParser;

import java.util.List;
import java.util.Locale;

class TestComponentRenderer {

    private final TemplateCache cache = new TemplateCache(TemplateParser.INSTANCE);
    private final ComponentTemplateFormatter componentFormatter =
            ComponentTemplateFormatter.defaultString(Component::text);

    private final ComponentRenderer format = new ComponentRenderer(cache, componentFormatter) {
        @Override
        public Component deserializeInput(String text) {
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
        var f = new ComponentRenderer(cache, ComponentTemplateFormatter.defaultString(s
                -> Component.text(s).color(NamedTextColor.RED))) {
            @Override
            public Component deserializeInput(String text) {
                return Component.text(text);
            }
        };
        var ctx = ctxWithInfo("test2");
        var r = f.render(ctx, "%s", List.of("red"));
        Assertions.assertEquals(Component.empty()
                .append(Component.text("red")
                        .color(NamedTextColor.RED)), r);
    }

    @Test
    void testLegacyRender() {
        var s = LegacyComponentSerializer.builder()
                .extractUrls().hexColors().character('&').build();
        var f = new LegacyComponentRenderer(cache, s);
        var ctx = ctxWithInfo("test3");
        var r = f.render(ctx, "&c%s", List.of("red"));
        // style of original is merged with root so red is entire text instead of just child
        Assertions.assertEquals(Component.empty()
                .append(Component.text("red"))
                .color(NamedTextColor.RED), r);
    }

    @Test
    void testLegacyRenderLegacyArg() {
        var s = LegacyComponentSerializer.builder()
                .extractUrls().hexColors().character('&').build();
        var ctf = ComponentTemplateFormatter.defaultStringLegacy(s);
        var f = new LegacyComponentRenderer(cache, ctf, s);
        var ctx = ctxWithInfo("test3");
        var r = f.render(ctx, "&c%s", List.of("&6gold"));
        // style of original is merged with root so red is entire text instead of just child, then child is golden
        Assertions.assertEquals(Component.empty()
                .append(Component.text("gold").color(NamedTextColor.GOLD))
                .color(NamedTextColor.RED), r);
    }

    @Test
    void testLegacyRender2() {
        var s = LegacyComponentSerializer.builder()
                .extractUrls().hexColors().character('&').build();
        var f = new LegacyComponentRenderer(cache, s);
        var ctx = ctxWithInfo("test3");
        var r = f.render(ctx, "-&c%s", List.of("red"));
        // IDK how actual component is produced and what is part of our merging or legacy serializer
        Assertions.assertEquals(Component.empty()
                .append(Component.text("-"),
                        Component.empty().color(NamedTextColor.RED)
                                .append(Component.text("red"))), r);
    }

    @Test
    void testMmRender() {
        var s = MiniMessage.miniMessage();
        var f = new MiniMessageComponentRenderer(cache, s);
        var ctx = ctxWithInfo("test3");
        var r = f.render(ctx, "<red>%s</red>", List.of("red"));
        // style of original is merged with root so red is entire text instead of just child
        Assertions.assertEquals(Component.empty()
                .append(Component.text("red"))
                .color(NamedTextColor.RED), r);
    }

    @Test
    void testMmRender2() {
        var s = MiniMessage.miniMessage();
        var f = new MiniMessageComponentRenderer(cache, s);
        var ctx = ctxWithInfo("test3");
        var r = f.render(ctx, "-<red>%s</red>", List.of("red"));
        // red text component is inputted into formatter so result for <red>%s</red will be red empty component,
        // and then It's added to empty root component after -
        Assertions.assertEquals(Component.empty().append(
                Component.text("-"),
                Component.empty().color(NamedTextColor.RED)
                        .append(Component.text("red"))), r);
    }

    /**
     * here we're using legacy serializer for legacy argument converters so plain strings are processed,
     *  but original context is MM
     */
    @Test
    void testMmRenderComplex() {
        var ls = LegacyComponentSerializer.builder()
                .extractUrls().hexColors().character('&').build();
        var s = MiniMessage.miniMessage();
        var f = new MiniMessageComponentRenderer(cache, ComponentTemplateFormatter.defaultString(ls::deserialize), s);
        var ctx = ctxWithInfo("test3");
        var r = f.render(ctx, "<red>%s</red>", List.of("&6gold"));
        var ex = Component.empty()
                .color(NamedTextColor.RED)
                .append(Component.text("gold").color(NamedTextColor.GOLD));
        Assertions.assertEquals(ex, r);
    }

}
