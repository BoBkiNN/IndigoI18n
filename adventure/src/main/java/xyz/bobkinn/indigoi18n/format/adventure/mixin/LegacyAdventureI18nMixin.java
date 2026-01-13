package xyz.bobkinn.indigoi18n.format.adventure.mixin;

import net.kyori.adventure.text.Component;
import xyz.bobkinn.indigoi18n.context.Context;
import xyz.bobkinn.indigoi18n.format.I18nMixin;
import xyz.bobkinn.indigoi18n.format.adventure.AdventureFormats;

import java.util.Arrays;
import java.util.List;

/**
 * Mixin that adds parseA methods using
 * {@link net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer legacy}
 * adventure format for text parsing and then producing {@link Component}
 */
@SuppressWarnings("unused")
public interface LegacyAdventureI18nMixin extends I18nMixin {
    default Component parseA(Context ctx, String lang, String key, List<Object> args) {
        return parse(AdventureFormats.LEGACY, ctx, lang, key, args);
    }

    default Component parseA(Context ctx, String lang, String key, Object... args) {
        return parseA(ctx, lang, key, Arrays.asList(args));
    }

    default Component parseA(String lang, String key, List<Object> args) {
        return parseA(newContext(lang, key), lang, key, args);
    }

    default Component parseA(String lang, String key, Object... args) {
        return parseA(newContext(lang, key), lang, key, Arrays.asList(args));
    }
}
