package xyz.bobkinn.indigoi18n.template.format;

import xyz.bobkinn.indigoi18n.context.Context;
import xyz.bobkinn.indigoi18n.context.impl.InlineContext;
import xyz.bobkinn.indigoi18n.context.impl.LangKeyContext;
import xyz.bobkinn.indigoi18n.data.ParsedEntry;
import xyz.bobkinn.indigoi18n.template.InlineTranslation;
import xyz.bobkinn.indigoi18n.template.TemplateVisitor;
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
                formatArgument(result, arg, p);
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

    private static void formatInline(InlineTranslation inline, Context ctx, StringBuilder result, List<Object> params) {
        int cd = ctx.getOptional(InlineContext.class)
                .map(InlineContext::getRemainingDepth)
                .orElse(inline.getMaxDepth());
        var key = inline.getKey();
        if (cd <= 0) {
            // no remaining depth
            throw new IllegalStateException("Depth limit exceeded");
        }
        var i18n = ctx.resolveI18n();
        var sub = ctx.sub();
        sub.set(new InlineContext(cd-1));
        String targetLang;
        if (inline.getLang() != null) {
            targetLang = inline.getLang();
        } else {
            targetLang = ctx.resolveOptional(LangKeyContext.class)
                    .map(LangKeyContext::getLang)
                    .orElseThrow(() -> new IllegalStateException("No language in current context tree"));
        }
        var res = i18n.parse(String.class, sub, targetLang, key, params);
        result.append(res);
    }
}
