package xyz.bobkinn.indigoi18n.spigot;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import xyz.bobkinn.indigoi18n.format.adventure.AdventureI18n;

/**
 * Same as {@link AdventureI18n} but also with 3 mixins using {@link CommandSenderLanguageMixin}
 * @see AdventureI18n
 */
@SuppressWarnings("unused")
public class SpigotAdventureI18n extends AdventureI18n implements MiniMessageAdventureSpigotI18nMixin,
        LegacyAdventureSpigotI18nMixin, StringSpigotI18nMixin {
    public SpigotAdventureI18n() {
        super();
    }

    public SpigotAdventureI18n(LegacyComponentSerializer serializer, MiniMessage miniMessage) {
        super(serializer, miniMessage);
    }
}
