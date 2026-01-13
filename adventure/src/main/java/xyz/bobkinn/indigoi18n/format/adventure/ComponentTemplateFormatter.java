package xyz.bobkinn.indigoi18n.format.adventure;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import xyz.bobkinn.indigoi18n.context.Context;
import xyz.bobkinn.indigoi18n.data.ParsedEntry;
import xyz.bobkinn.indigoi18n.template.InlineTranslation;
import xyz.bobkinn.indigoi18n.template.TemplateVisitor;
import xyz.bobkinn.indigoi18n.template.arg.ArgConverters;
import xyz.bobkinn.indigoi18n.template.arg.ArgumentConverter;
import xyz.bobkinn.indigoi18n.template.arg.TemplateArgument;
import xyz.bobkinn.indigoi18n.template.format.FormatPattern;
import xyz.bobkinn.indigoi18n.template.format.StringTemplateFormatter;
import xyz.bobkinn.indigoi18n.template.format.TemplateFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;


/**
 * This template formatter produces component from entire string and then create empty component
 * with original styles and converted children.
 * This made to not lost any style information while parsing template
 */
@Getter
public class ComponentTemplateFormatter extends TemplateFormatter<Component> {
    private static final @NotNull TextComponent NULL_COMPONENT = Component.text("null");
    public static final ArgumentConverter<TextComponent, Component> TEXT_COMPONENT_CONVERTER
            = (ctx, argument, format) -> {
        var nc = ArgConverters.STRING_CONVERTER.format(ctx, argument.content(), format);
        return argument.content(nc);
    };

    private final TemplateFormatter<String> stringTemplateFormatter;

    private final Function<String, Component> legacyConverter;

    /**
     *
     * @param stringTemplateFormatter string template converter used to handle and replace text component contents
     * @param legacyConverter if not null, legacy converter is used to convert String argument into Component
     */
    public ComponentTemplateFormatter(TemplateFormatter<String> stringTemplateFormatter,
                                      Function<String, Component> legacyConverter) {
        this.stringTemplateFormatter = stringTemplateFormatter;
        this.legacyConverter = legacyConverter;
        registerDefaultConverters();
    }

    /**
     * A utility method to create new instance with {@link StringTemplateFormatter}
     * @param legacyConverter legacy converter
     * @see #ComponentTemplateFormatter(TemplateFormatter, Function)  ComponentTemplateFormatter
     */
    @Contract("_ -> new")
    public static @NotNull ComponentTemplateFormatter defaultString(Function<String, Component> legacyConverter) {
        return new ComponentTemplateFormatter(new StringTemplateFormatter(), legacyConverter);
    }

    @Override
    protected void registerDefaultConverters() {
        // currently we will support only TextComponent formatting, not sure what to do with other types
        addConverter(TextComponent.class, TEXT_COMPONENT_CONVERTER);
        // pass Component as is so it do not handled with string template formatter
        // Maybe this probably should be moved to formatArgument logic so subclasses won't need to specify it every time,
        //  but I think it's ok
        addConverter(Component.class, ArgumentConverter.noOp());
        // String -> legacyToAdventure for compatibility so passing arg like '&cArg' displays color too.
        //  With handling childless text we know exact text length to perform aligning etc.
        if (legacyConverter != null) {
            addConverter(String.class, (ctx, argument, format) -> {
                var c = legacyConverter.apply(argument);
                if (c == null) return null;
                if (c.children().isEmpty() && c instanceof TextComponent tc) {
                    return TEXT_COMPONENT_CONVERTER.format(ctx, tc, format);
                }
                return c;
            });
        }
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
        var sc = stringTemplateFormatter.resolveConverter(value);
        if (sc != null) return sc.mapOut(this::createText);
        return null;
    }

    public Component formatArgument(Context ctx, TemplateArgument arg, Object value) {
        // TODO we should do something about repeating this part. Guess representations are common
        var format = arg.getPattern();
        Objects.requireNonNull(format, "no format set for argument " + arg);
        if (arg.isRepr('r')) {
            var rawRepr = stringTemplateFormatter.createRawRepr(value);
            var res = ArgConverters.STRING_CONVERTER.format(ctx, rawRepr, format);
            return createText(res);
        }
        if (arg.isRepr('h', 'H')) {
            return createText(String.format("%" + arg.getRepr(), value));
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
        // query shared seq arg or create new.
        var sq = ctx.getOptional(SharedSeqArgContext.class)
                .orElseGet(SharedSeqArgContext::new);
        final int[] ic = {0};
        var ft = resolveFormatType(ctx);
        List<Component> extra = new ArrayList<>(entry.parts().size());
        entry.visit(new TemplateVisitor() {
            @Override
            public void visitPlain(String text) {
                extra.add(Component.text(text));
            }

            @Override
            public void visitArgument(TemplateArgument arg) {
                if (!arg.isHasExplicitIndex()) {
                    // increase implicit index count
                    ic[0]++;
                }
                var idx = arg.getIndex();
                if (idx >= params.size()) {
                    // unknown argument
                    extra.add(Component.text("%" + (idx + 1)));
                    return;
                }
                var p = params.get(idx);
                var res = formatArgument(ctx, arg, p);
                extra.add(res);
            }

            @Override
            public void visitInline(InlineTranslation inline) {
                // fallback to plain formatter
                var tft = ft != null ? ft : AdventureFormats.PLAIN;
                var il = formatInline(tft, inline, ctx, params);
                extra.add(il);
            }
        });
        // set increased value
        ctx.set(sq.inc(ic[0]));
        return Component.empty().children(extra);
    }

}
