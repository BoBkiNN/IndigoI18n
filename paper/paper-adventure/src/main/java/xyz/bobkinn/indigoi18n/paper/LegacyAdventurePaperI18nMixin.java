package xyz.bobkinn.indigoi18n.paper;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import xyz.bobkinn.indigoi18n.context.Context;
import xyz.bobkinn.indigoi18n.render.adventure.mixin.LegacyAdventureI18nMixin;

import java.util.List;

@SuppressWarnings("unused")
public interface LegacyAdventurePaperI18nMixin extends LegacyAdventureI18nMixin, CommandSenderLanguageI18nMixin {
    default Component parseA(Context ctx, CommandSender viewer, String key, List<Object> args) {
        var l = getLanguage(viewer);
        return parseA(viewerCtx(ctx, viewer, key, l), l, key, args);
    }

    default Component parseA(Context ctx, CommandSender viewer, String key, Object... args) {
        var l = getLanguage(viewer);
        return parseA(viewerCtx(ctx, viewer, key, l), l, key, args);
    }

    default Component parseA(CommandSender viewer, String key, List<Object> args) {
        var l = getLanguage(viewer);
        return parseA(viewerCtx(viewer, key, l), l, key, args);
    }

    default Component parseA(CommandSender viewer, String key, Object... args) {
        var l = getLanguage(viewer);
        return parseA(viewerCtx(viewer, key, l), l, key, args);
    }
}
