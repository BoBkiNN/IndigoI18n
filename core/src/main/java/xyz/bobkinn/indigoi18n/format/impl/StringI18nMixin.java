package xyz.bobkinn.indigoi18n.format.impl;

import xyz.bobkinn.indigoi18n.format.I18nMixin;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public interface StringI18nMixin extends I18nMixin {
    default String parse(String lang, String key, List<Object> args) {
        return parse(String.class, lang, key, args);
    }

    default String parse(String lang, String key, Object... args) {
        return parse(lang, key, Arrays.asList(args));
    }
}
