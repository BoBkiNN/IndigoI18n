package xyz.bobkinn.indigoi18n.format.impl;

import xyz.bobkinn.indigoi18n.TemplateCache;
import xyz.bobkinn.indigoi18n.data.TranslationInfo;
import xyz.bobkinn.indigoi18n.format.I18nFormat;

import java.util.List;

public class StringI18nFormat extends I18nFormat<String> {

    public StringI18nFormat(TemplateCache cache) {
        super(cache);
    }

    @Override
    public String produce(String text) {
        return text;
    }

    @Override
    public String replaceArguments(TranslationInfo info, String input, List<Object> args) {
        var parsed = cache.getOrCreate(input, info);
        if (parsed == null) {
            return info.key();
        }
        return replaceArgs(parsed, args);
    }
}
