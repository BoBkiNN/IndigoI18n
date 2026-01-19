package xyz.bobkinn.indigoi18n.spigot;

import org.bukkit.command.CommandSender;
import xyz.bobkinn.indigoi18n.context.Context;
import xyz.bobkinn.indigoi18n.format.impl.StringI18nMixin;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public interface StringSpigotI18nMixin extends StringI18nMixin, CommandSenderLanguageMixin {
    default String parse(Context ctx, CommandSender viewer, String key, List<Object> args) {
        return parse(ctx, getLanguage(viewer), key, args);
    }

    default String parse(Context ctx, CommandSender viewer, String key, Object... args) {
        return parse(ctx, getLanguage(viewer), key, Arrays.asList(args));
    }

    default String parse(CommandSender viewer, String key, List<Object> args) {
        return parse(getLanguage(viewer), key, args);
    }

    default String parse(CommandSender viewer, String key, Object... args) {
        return parse(getLanguage(viewer), key, args);
    }
}
