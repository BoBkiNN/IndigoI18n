package xyz.bobkinn.indigoi18n.template.format;

import xyz.bobkinn.indigoi18n.data.ParsedEntry;
import xyz.bobkinn.indigoi18n.template.arg.ArgConverters;
import xyz.bobkinn.indigoi18n.template.arg.TemplateArgument;

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
    }

    @Override
    public String createText(String value) {
        return value;
    }

    private String formatNull(FormatPattern format) {
        var nConv = getConverter(null);
        if (nConv != null) return nConv.format(null, format);
        return "null";
    }

    private void formatArgument(StringBuilder builder, TemplateArgument arg, Object value) {
        var format = arg.getPattern();
        Objects.requireNonNull(format, "no format set for argument "+arg);
        if (arg.isRepr('r')) {
            var rawRepr = createRawRepr(value);
            var res = ArgConverters.STRING_CONVERTER.format(rawRepr, format);
            builder.append(res);
            return;
        }
        if (arg.isRepr('h', 'H')) {
            builder.append(String.format("%"+arg.getRepr(), value));
            return;
        }
        if (arg.isRepr('s') && (value == null || value.getClass() != String.class)) {
            // !s converts any object (except string) to string and then formats using string converter
            var res = ArgConverters.STRING_CONVERTER.format(String.valueOf(value), format);
            builder.append(res);
            return;
        }
        if (value == null) {
            var res = formatNull(format);
            builder.append(res);
            return;
        }
        var conv = getConverter(value);
        if (conv == null) {
            var res = ArgConverters.STRING_CONVERTER.format(String.valueOf(value), format);
            builder.append(res);
            return;
        }
        var content = conv.format(value, format);
        builder.append(content);
    }

    @Override
    public String format(ParsedEntry entry, List<Object> params) {
        var result = new StringBuilder();
        entry.process(result::append, arg -> {
            var idx = arg.getIndex();
            if (idx >= params.size()) {
                // unknown argument
                result.append("%").append(idx+1);
                return;
            }
            var p = params.get(idx);
            formatArgument(result, arg, p);
        });
        return result.toString();
    }
}
