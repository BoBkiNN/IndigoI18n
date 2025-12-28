package xyz.bobkinn.indigoi18n.format.adventure;

import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;
import xyz.bobkinn.indigoi18n.context.Context;
import xyz.bobkinn.indigoi18n.data.ParsedEntry;
import xyz.bobkinn.indigoi18n.template.InlineTranslation;
import xyz.bobkinn.indigoi18n.template.TemplateVisitor;
import xyz.bobkinn.indigoi18n.template.arg.ArgConverters;
import xyz.bobkinn.indigoi18n.template.arg.ArgumentConverter;
import xyz.bobkinn.indigoi18n.template.arg.TemplateArgument;
import xyz.bobkinn.indigoi18n.template.format.FormatPattern;
import xyz.bobkinn.indigoi18n.template.format.TemplateFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// TODO optional style leaking. When enabled, inserting '&cArg' into '%s-text' will produce '<red>Arg-text'
//  instead of '<red>Arg</red>-text'.
//  Leaked style is spread unless new style is specified. Not sure of correct rules yet
/**
 * This template formatter produces component from entire string and then create empty component
 * with original styles and converted children.
 * This made to not lost any style information while parsing template
 */
@RequiredArgsConstructor
public class ComponentTemplateFormatter extends TemplateFormatter<Component> {
    private static final @NotNull TextComponent NULL_COMPONENT = Component.text("null");
    public static final ArgumentConverter<TextComponent, Component> TEXT_COMPONENT_CONVERTER
            = ArgConverters.STRING_CONVERTER.map(TextComponent::content, Component::text);

    private final TemplateFormatter<String> stringTemplateFormatter;

    @Override
    protected void registerDefaultConverters() {
        // currently we will support only TextComponent formatting, not sure what to do with other types
        addConverter(TextComponent.class, TEXT_COMPONENT_CONVERTER);
        // pass Component as is so it do not handled with string template formatter
        // TODO this probably should be moved to formatArgument logic so subclasses wont need to specify it every time
        addConverter(Component.class, ArgumentConverter.noOp());
        // TODO String -> legacyToAdventure for compatibility so passing arg like '&cArg' displays color too.
        //  must be configurable to disable it or change conversion function.
        //  But unsure how to apply format if we convert it, maybe handling only TextComponent without children?
        //  With handling childless text we know exact text length to perform aligning and etc.
        //  Other display properties are unknown.
    }

    @Override
    public Component createText(String value) {
        return Component.text(value);
    }

    private Component formatNull(Context ctx, FormatPattern format) {
        var nConv = getConverter(null);
        if (nConv != null) return nConv.format(ctx, null, format);
        return NULL_COMPONENT;
    }

    public <T> ArgumentConverter<T, Component> findConverter(T value) {
        var cv = resolveConverter(value);
        if (cv != null) return cv;
        // if we don't have converter to Component, fall back to string template formatter
        var sc = stringTemplateFormatter.getConverter(value);
        if (sc != null) return sc.mapOut(this::createText);
        return null;
    }

    public Component formatArgument(Context ctx, TemplateArgument arg, Object value) {
        // TODO we should do something about repeating this part. Guess representations are common
        var format = arg.getPattern();
        Objects.requireNonNull(format, "no format set for argument "+arg);
        if (arg.isRepr('r')) {
            var rawRepr = stringTemplateFormatter.createRawRepr(value);
            var res = ArgConverters.STRING_CONVERTER.format(ctx, rawRepr, format);
            return createText(res);
        }
        if (arg.isRepr('h', 'H')) {
            return createText(String.format("%"+arg.getRepr(), value));
        }
        if (arg.isRepr('s') && (value == null || value.getClass() != String.class)) {
            // !s converts any object (except string) to string and then formats using string converter
            var res = ArgConverters.STRING_CONVERTER.format(ctx, String.valueOf(value), format);
            return createText(res);
        }
        if (value == null) {
            return formatNull(ctx, format);
        }
        var conv = findConverter(value);
        if (conv != null) {
            return conv.format(ctx, value, format);
        }
        var res = ArgConverters.STRING_CONVERTER.format(ctx, String.valueOf(value), format);
        return createText(res);
    }

    /**
     * Creates new empty component with children set to resulting parts
     */
    @Override
    public Component format(Context ctx, ParsedEntry entry, List<Object> params) {
        List<Component> extra = new ArrayList<>(entry.parts().size());
        entry.visit(new TemplateVisitor() {
            @Override
            public void visitPlain(String text) {
                extra.add(Component.text(text));
            }

            @Override
            public void visitArgument(TemplateArgument arg) {
                var idx = arg.getIndex();
                if (idx >= params.size()) {
                    // unknown argument
                    extra.add(Component.text("%"+(idx+1)));
                    return;
                }
                var p = params.get(idx);
                var res = formatArgument(ctx, arg, p);
                extra.add(res);
            }

            @Override
            public void visitInline(InlineTranslation inline) {
                var il = formatInline(Component.class, inline, ctx, params);
                extra.add(il);
            }
        });
        return Component.empty().children(extra);
    }

}
