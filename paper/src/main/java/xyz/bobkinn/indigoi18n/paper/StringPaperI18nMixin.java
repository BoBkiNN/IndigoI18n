package xyz.bobkinn.indigoi18n.paper;

import org.bukkit.command.CommandSender;
import xyz.bobkinn.indigoi18n.context.Context;
import xyz.bobkinn.indigoi18n.format.impl.StringI18nMixin;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public interface StringPaperI18nMixin extends StringI18nMixin, CommandSenderLanguageMixin {
    default String parse(Context ctx, CommandSender viewer, String key, List<Object> args) {
        return parse(injectCtx(ctx, viewer), getLanguage(viewer), key, args);
    }

    default String parse(Context ctx, CommandSender viewer, String key, Object... args) {
        return parse(injectCtx(ctx, viewer), getLanguage(viewer), key, Arrays.asList(args));
    }

    default String parse(CommandSender viewer, String key, List<Object> args) {
        return parse(viewerCtx(viewer), getLanguage(viewer), key, args);
    }

    default String parse(CommandSender viewer, String key, Object... args) {
        return parse(viewerCtx(viewer), getLanguage(viewer), key, args);
    }
}
