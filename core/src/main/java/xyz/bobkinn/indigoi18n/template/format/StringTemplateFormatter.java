package xyz.bobkinn.indigoi18n.template.format;

import xyz.bobkinn.indigoi18n.context.Context;
import xyz.bobkinn.indigoi18n.data.ParsedEntry;
import xyz.bobkinn.indigoi18n.template.InlineTranslation;
import xyz.bobkinn.indigoi18n.template.TemplateVisitor;
import xyz.bobkinn.indigoi18n.template.arg.ArgConverters;
import xyz.bobkinn.indigoi18n.template.arg.TemplateArgument;

import java.time.*;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;

public class StringTemplateFormatter extends TemplateFormatter<String> {

    @Override
    protected void registerDefaultConverters() {
        addConverter(String.class, ArgConverters.STRING_CONVERTER);

        addConverter(Byte.class, ArgConverters.INT_CONVERTER);
        addConverter(Short.class, ArgConverters.INT_CONVERTER);
        addConverter(Integer.class, ArgConverters.INT_CONVERTER);
        addConverter(Long.class, ArgConverters.INT_CONVERTER);

        addConverter(Double.class, ArgConverters.NUMBER_CONVERTER);
        addConverter(Float.class, ArgConverters.NUMBER_CONVERTER);

        // java.time
        addConverter(LocalDateTime.class, ArgConverters.TEMPORAL_CONVERTER);
        addConverter(LocalDate.class, ArgConverters.TEMPORAL_CONVERTER);
        addConverter(LocalTime.class, ArgConverters.TEMPORAL_CONVERTER);

        addConverter(ZonedDateTime.class, ArgConverters.TEMPORAL_CONVERTER);
        addConverter(OffsetDateTime.class, ArgConverters.TEMPORAL_CONVERTER);
        addConverter(OffsetTime.class, ArgConverters.TEMPORAL_CONVERTER);
        addConverter(Instant.class, ArgConverters.TEMPORAL_CONVERTER);

        // legacy
        addConverter(GregorianCalendar.class, ArgConverters.CALENDAR_CONVERTER);
        addConverter(Date.class, ArgConverters.DATE_CONVERTER);
    }

    @Override
    public String createText(String value) {
        return value;
    }

    private String formatNull(Context ctx, FormatPattern format) {
        var nConv = getConverter(null);
        if (nConv != null) return nConv.format(ctx, null, format);
        return "null";
    }

    private void formatArgument(Context ctx, StringBuilder builder, TemplateArgument arg, Object value) {
        var format = arg.getPattern();
        Objects.requireNonNull(format, "no format set for argument "+arg);
        if (arg.isRepr('r')) {
            var rawRepr = createRawRepr(value);
            var res = ArgConverters.STRING_CONVERTER.format(ctx, rawRepr, format);
            builder.append(res);
            return;
        }
        if (arg.isRepr('h', 'H')) {
            builder.append(String.format("%"+arg.getRepr(), value));
            return;
        }
        if (arg.isRepr('s') && (value == null || value.getClass() != String.class)) {
            // !s converts any object (except string) to string and then formats using string converter
            var res = ArgConverters.STRING_CONVERTER.format(ctx, String.valueOf(value), format);
            builder.append(res);
            return;
        }
        if (value == null) {
            var res = formatNull(ctx, format);
            builder.append(res);
            return;
        }
        var conv = getConverter(value);
        if (conv == null) {
            var res = ArgConverters.STRING_CONVERTER.format(ctx, String.valueOf(value), format);
            builder.append(res);
            return;
        }
        var content = conv.format(ctx, value, format);
        builder.append(content);
    }

    @Override
    public String format(Context ctx, ParsedEntry entry, List<Object> params) {
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
                    result.append("%").append(idx+1);
                    return;
                }
                var p = params.get(idx);
                formatArgument(ctx, result, arg, p);
            }

            @Override
            public void visitInline(InlineTranslation inline) {
                try {
                    formatInline(inline, ctx, result, params);
                } catch (Exception e) {
                    var key = ctx.key();
                    result.append("<").append(key).append(">");
                }
            }
        });
        return result.toString();
    }

    private void formatInline(InlineTranslation inline, Context ctx, StringBuilder result, List<Object> params) {
        var res = formatInline(String.class, inline, ctx, params);
        result.append(res);
    }
}
