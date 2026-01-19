package xyz.bobkinn.indigoi18n.spigot;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

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
        if (sender instanceof Player p) return p.getLocale();
        return getDefaultLanguage();
    }
}
