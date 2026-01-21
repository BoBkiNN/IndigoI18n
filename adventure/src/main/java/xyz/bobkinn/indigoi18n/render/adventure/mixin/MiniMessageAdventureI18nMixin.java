package xyz.bobkinn.indigoi18n.render.adventure.mixin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import xyz.bobkinn.indigoi18n.context.Context;
import xyz.bobkinn.indigoi18n.render.I18nMixin;
import xyz.bobkinn.indigoi18n.render.adventure.AdventureRenderers;

import java.util.Arrays;
import java.util.List;

/**
 * Mixin that adds parseMM methods using {@link  MiniMessage} adventure format for text parsing and then producing {@link Component}
 */
@SuppressWarnings("unused")
public interface MiniMessageAdventureI18nMixin extends I18nMixin {
    default Component parseMM(Context ctx, String lang, String key, List<Object> args) {
        return parse(AdventureRenderers.MINI_MESSAGE, ctx, lang, key, args);
    }

    default Component parseMM(Context ctx, String lang, String key, Object... args) {
        return parseMM(ctx, lang, key, Arrays.asList(args));
    }

    default Component parseMM(String lang, String key, List<Object> args) {
        return parseMM(newContext(lang, key), lang, key, args);
    }

    default Component parseMM(String lang, String key, Object... args) {
        return parseMM(newContext(lang, key), lang, key, Arrays.asList(args));
    }
}
