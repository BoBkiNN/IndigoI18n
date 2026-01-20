package xyz.bobkinn.indigoi18n.template.format;

import xyz.bobkinn.indigoi18n.context.Context;
import xyz.bobkinn.indigoi18n.data.ParsedEntry;
import xyz.bobkinn.indigoi18n.format.FormatType;
import xyz.bobkinn.indigoi18n.template.InlineTranslation;
import xyz.bobkinn.indigoi18n.template.TemplateVisitor;
import xyz.bobkinn.indigoi18n.template.arg.ArgConverters;
import xyz.bobkinn.indigoi18n.template.arg.TemplateArgument;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.temporal.Temporal;
import java.util.*;

public class StringTemplateFormatter extends TemplateFormatter<String> {

    @Override
    protected void registerDefaultConverters() {
        putConverter(String.class, ArgConverters.STRING_CONVERTER);

        putConverter(Byte.class, ArgConverters.INT_CONVERTER);
        putConverter(Short.class, ArgConverters.INT_CONVERTER);
        putConverter(Integer.class, ArgConverters.INT_CONVERTER);
        putConverter(Long.class, ArgConverters.INT_CONVERTER);

        putConverter(Number.class, ArgConverters.NUMBER_CONVERTER);

        putConverter(BigInteger.class, ArgConverters.BIG_INT_CONVERTER);
        putConverter(BigDecimal.class, ArgConverters.BIG_DECIMAL_CONVERTER);

        // java.time
        putConverter(Temporal.class, ArgConverters.TEMPORAL_CONVERTER);

        // legacy
        putConverter(Calendar.class, ArgConverters.CALENDAR_CONVERTER);
        putConverter(Date.class, ArgConverters.DATE_CONVERTER);
    }

    @Override
    public String createText(String value) {
        return value;
    }

    @Override
    public String formatNull(Context ctx, FormatPattern format) {
        var nConv = getConverter(null);
        if (nConv != null) return nConv.format(ctx, null, format);
        return "null";
    }

    private void formatArgument(Context ctx, StringBuilder builder, TemplateArgument arg, Object value) {
        var format = arg.getPattern();
        Objects.requireNonNull(format, "no format set for argument "+arg);
        var repr = formatRepresentation(ctx, arg, value, this::createRawRepr);
        if (repr != null) {
            builder.append(repr);
            return;
        }
        if (value == null) {
            var res = formatNull(ctx, format);
            builder.append(res);
            return;
        }
        var conv = resolveConverter(value);
        if (conv == null) {
            var res = ArgConverters.STRING_CONVERTER.format(ctx, String.valueOf(value), format);
            builder.append(res);
            return;
        }
        var content = conv.format(ctx, value, format);
        builder.append(content);
    }

    /**
     * Called when index of passed argument is greater than size of {@code params}.
     * Default implementation appends % and argument index + 1 to string builder.
     */
    @SuppressWarnings("unused")
    protected void onUnknownArgument(StringBuilder result, Context ctx, ParsedEntry entry,
                                     List<Object> params, TemplateArgument arg) {
        result.append("%").append(arg.getIndex()+1);
    }

    /**
     * Called when inlining of {@link InlineTranslation}
     * by {@link #formatInline(FormatType, InlineTranslation, Context, List)} failed.<br>
     * Default implementations appends {@code <inline.key>} text to string builder.
     * @param exception exception produced by formatInline method.
     */
    @SuppressWarnings("unused")
    protected void onFailedInline(StringBuilder result, Context ctx, ParsedEntry entry,
                                     List<Object> params, InlineTranslation inline, Exception exception) {
        var key = inline.getKey();
        result.append("<").append(key).append(">");
    }

    @Override
    public String format(Context ctx, ParsedEntry entry, List<Object> params) {
        var ft = resolveFormatType(ctx);
        // fallback to string format type
        var tft = ft != null ? ft : FormatType.STRING_FORMAT_TYPE;
        var result = new StringBuilder();
        entry.visit(new TemplateVisitor() {
            @Override
            public void visitPlain(String text) {
                result.append(text);
            }

            @Override
            public void visitArgument(TemplateArgument arg) {
                var idx = arg.getIndex();
                if (idx >= params.size()) {
                    // unknown argument
                    onUnknownArgument(result, ctx, entry, params, arg);
                    return;
                }
                var p = params.get(idx);
                formatArgument(ctx, result, arg, p);
            }

            @Override
            public void visitInline(InlineTranslation inline) {
                try {
                    formatInline(tft, inline, ctx, result, params);
                } catch (Exception e) {
                    onFailedInline(result, ctx, entry, params, inline, e);
                }
            }
        });
        return result.toString();
    }

    private void formatInline(FormatType<String> ft, InlineTranslation inline, Context ctx, StringBuilder result, List<Object> params) {
        var res = formatInline(ft, inline, ctx, params);
        result.append(res);
    }
}
