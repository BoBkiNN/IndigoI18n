package xyz.bobkinn.indigoi18n.paper;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Viewer language resolver is used to lookup language id for {@link CommandSender viewer}.<br>
 * You can override default methods to attach your own behaviour like player locale storage.
 * @see #getPlayerLanguage(Player) 
 * @see #getLanguage(CommandSender) 
 * @see #DEFAULT_RESOLVER
 */
public interface ViewerLanguageResolver {

    /**
     * Instance with no overrides
     */
    ViewerLanguageResolver DEFAULT_RESOLVER = new ViewerLanguageResolver() {

    };

    /**
     * @return default language to use when viewer is not player, console or null
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
     * Get player language id
     * @param player viewer
     * @return language id (code)
     */
    @SuppressWarnings("deprecation")
    default String getPlayerLanguage(@NotNull Player player) {
        return player.getLocale();
    }

    /**
     * Get language of viewer. Default implementation calls other method based on type of viewer
     * @param viewer entity whose language is used
     * @return language of viewer or default language.
     * @see #getDefaultLanguage()
     * @see #getConsoleLanguage()
     * @see #getPlayerLanguage(Player)
     */
    default String getLanguage(CommandSender viewer) {
        if (viewer == null) return getDefaultLanguage();
        if (viewer instanceof ConsoleCommandSender) return getConsoleLanguage();
        if (viewer instanceof Player p) return getPlayerLanguage(p);
        return getDefaultLanguage();
    }
}
