package xyz.bobkinn.indigoi18n.paper;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import xyz.bobkinn.indigoi18n.context.Context;

public interface CommandSenderLanguageMixin {

    /**
     * @return default language to use when sender is not player
     */
    default String getDefaultLanguage() {
        return "en_us";
    }

    /**
     * Default implementation is {@link #getDefaultLanguage()}
     * @return language that will be used for {@link ConsoleCommandSender}
     */
    default String getConsoleLanguage() {
        return getDefaultLanguage();
    }

    /**
     * Get language of sender
     * @param sender entity whose language is used
     * @return language of sender or default language.
     * @see #getDefaultLanguage()
     * @see #getConsoleLanguage()
     */
    default String getLanguage(CommandSender sender) {
        if (sender == null) return getDefaultLanguage();
        if (sender instanceof ConsoleCommandSender) return getConsoleLanguage();
        if (sender instanceof Player p) //noinspection deprecation
            return p.getLocale();
        return getDefaultLanguage();
    }

    default Context injectCtx(Context ctx, CommandSender viewer) {
        if (ctx == null) return new Context().with(new ViewerContext(viewer));
        // override previous viewer context. Guess it is better than keeping any existing.
        return ctx.with(new ViewerContext(viewer));
    }

    default Context viewerCtx(CommandSender viewer) {
        return new Context().with(new ViewerContext(viewer));
    }
}
