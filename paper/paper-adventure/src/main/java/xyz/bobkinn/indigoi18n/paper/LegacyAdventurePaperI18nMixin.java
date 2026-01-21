package xyz.bobkinn.indigoi18n.paper;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import xyz.bobkinn.indigoi18n.context.Context;
import xyz.bobkinn.indigoi18n.format.adventure.mixin.LegacyAdventureI18nMixin;

import java.util.List;

@SuppressWarnings("unused")
public interface LegacyAdventurePaperI18nMixin extends LegacyAdventureI18nMixin, CommandSenderLanguageMixin {
    default Component parseA(Context ctx, CommandSender viewer, String key, List<Object> args) {
        return parseA(viewerCtx(ctx, viewer), getLanguage(viewer), key, args);
    }

    default Component parseA(Context ctx, CommandSender viewer, String key, Object... args) {
        return parseA(viewerCtx(ctx, viewer), getLanguage(viewer), key, args);
    }

    default Component parseA(CommandSender viewer, String key, List<Object> args) {
        return parseA(viewerCtx(viewer), getLanguage(viewer), key, args);
    }

    default Component parseA(CommandSender viewer, String key, Object... args) {
        return parseA(viewerCtx(viewer), getLanguage(viewer), key, args);
    }
}
