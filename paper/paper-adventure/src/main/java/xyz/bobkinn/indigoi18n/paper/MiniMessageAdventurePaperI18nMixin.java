package xyz.bobkinn.indigoi18n.paper;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import xyz.bobkinn.indigoi18n.context.Context;
import xyz.bobkinn.indigoi18n.format.adventure.mixin.MiniMessageAdventureI18nMixin;

import java.util.List;

@SuppressWarnings("unused")
public interface MiniMessageAdventurePaperI18nMixin extends MiniMessageAdventureI18nMixin, CommandSenderLanguageI18nMixin {
    default Component parseMM(Context ctx, CommandSender viewer, String key, List<Object> args) {
        var l = getLanguage(viewer);
        return parseMM(viewerCtx(ctx, viewer, key, l), l, key, args);
    }

    default Component parseMM(Context ctx, CommandSender viewer, String key, Object... args) {
        var l = getLanguage(viewer);
        return parseMM(viewerCtx(ctx, viewer, key, l), l, key, args);
    }

    default Component parseMM(CommandSender viewer, String key, List<Object> args) {
        var l = getLanguage(viewer);
        return parseMM(viewerCtx(viewer, key, l), l, key, args);
    }

    default Component parseMM(CommandSender viewer, String key, Object... args) {
        var l = getLanguage(viewer);
        return parseMM(viewerCtx(viewer, key, l), l, key, args);
    }
}
