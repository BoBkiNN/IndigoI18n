package xyz.bobkinn.indigoi18n.paper;

import org.bukkit.command.CommandSender;
import xyz.bobkinn.indigoi18n.context.Context;
import xyz.bobkinn.indigoi18n.format.impl.StringI18nMixin;

import java.util.List;

@SuppressWarnings("unused")
public interface StringPaperI18nMixin extends StringI18nMixin, CommandSenderLanguageI18nMixin {
    default String parse(Context ctx, CommandSender viewer, String key, List<Object> args) {
        var l = getLanguage(viewer);
        return parse(viewerCtx(ctx, viewer, key, l), l, key, args);
    }

    default String parse(Context ctx, CommandSender viewer, String key, Object... args) {
        var l = getLanguage(viewer);
        return parse(viewerCtx(ctx, viewer, key, l), l, key, args);
    }

    default String parse(CommandSender viewer, String key, List<Object> args) {
        var l = getLanguage(viewer);
        return parse(viewerCtx(viewer, key, l), l, key, args);
    }

    default String parse(CommandSender viewer, String key, Object... args) {
        var l = getLanguage(viewer);
        return parse(viewerCtx(viewer, key, l), l, key, args);
    }
}
