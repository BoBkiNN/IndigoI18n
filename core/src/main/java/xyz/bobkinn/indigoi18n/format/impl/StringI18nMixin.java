package xyz.bobkinn.indigoi18n.format.impl;

import xyz.bobkinn.indigoi18n.context.Context;
import xyz.bobkinn.indigoi18n.format.I18nMixin;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public interface StringI18nMixin extends I18nMixin {
    default String parse(Context ctx, String lang, String key, List<Object> args) {
        return parse(String.class, ctx, lang, key, args);
    }

    default String parse(Context ctx, String lang, String key, Object... args) {
        return parse(ctx, lang, key, Arrays.asList(args));
    }

    default String parse(String lang, String key, List<Object> args) {
        return parse(newContext(lang, key), lang, key, args);
    }

    default String parse(String lang, String key, Object... args) {
        return parse(newContext(lang, key), lang, key, Arrays.asList(args));
    }
}
