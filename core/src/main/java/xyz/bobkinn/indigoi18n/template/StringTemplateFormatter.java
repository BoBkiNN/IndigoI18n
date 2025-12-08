package xyz.bobkinn.indigoi18n.template;

import xyz.bobkinn.indigoi18n.data.ParsedEntry;

import java.util.List;
import java.util.Objects;

public class StringTemplateFormatter extends TemplateFormatter<String> {

    @Override
    protected void registerDefaultConverters() {
        converters.put(String.class, ArgConverters.STRING_CONVERTER);

        converters.put(Byte.class, ArgConverters.INT_CONVERTER);
        converters.put(Short.class, ArgConverters.INT_CONVERTER);
        converters.put(Integer.class, ArgConverters.INT_CONVERTER);
        converters.put(Long.class, ArgConverters.INT_CONVERTER);

        converters.put(Double.class, ArgConverters.NUMBER_CONVERTER);
        converters.put(Float.class, ArgConverters.NUMBER_CONVERTER);
    }

    @Override
    public String createText(String value) {
        return value;
    }

    private void formatArgument(StringBuilder builder, TemplateArgument arg, Object value) {
        var format = arg.getFormatSpec();
        Objects.requireNonNull(format, "no format set for argument "+arg);
        var doRepr = format.isDoRepr();
        if (doRepr) {
            var repr = createRepr(value);
            var res = ArgConverters.STRING_CONVERTER.format(repr, format);
            builder.append(res);
            return;
        }
        if (value == null) {
            var res = ArgConverters.STRING_CONVERTER.format("null", format);
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
                result.append("%").append(idx);
                return;
            }
            var p = params.get(idx);
            formatArgument(result, arg, p);
        });
        return result.toString();
    }
}
