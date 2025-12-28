package xyz.bobkinn.indigoi18n.format.impl;

import xyz.bobkinn.indigoi18n.context.Context;
import xyz.bobkinn.indigoi18n.data.TemplateCache;
import xyz.bobkinn.indigoi18n.format.I18nFormat;
import xyz.bobkinn.indigoi18n.template.format.StringTemplateFormatter;
import xyz.bobkinn.indigoi18n.template.format.TemplateFormatter;

import java.util.List;

public class StringI18nFormat extends I18nFormat<String> {
    private final TemplateFormatter<String> templateFormatter;

    public StringI18nFormat(TemplateCache cache) {
        super(cache);
        templateFormatter = new StringTemplateFormatter();
    }

    @Override
    public String produce(String text) {
        return text;
    }

    @Override
    public String replaceArguments(Context ctx, String input, List<Object> args) {
        var parsed = cache.getOrCreate(ctx, input);
        if (parsed == null) {
            return ctx.key();
        }
        return templateFormatter.format(ctx, parsed, args);
    }
}
