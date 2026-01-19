package xyz.bobkinn.indigoi18n.format.adventure;

import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import xyz.bobkinn.indigoi18n.StringI18n;
import xyz.bobkinn.indigoi18n.format.adventure.format.ComponentI18nFormat;
import xyz.bobkinn.indigoi18n.format.adventure.format.LegacyComponentI18nFormat;
import xyz.bobkinn.indigoi18n.format.adventure.format.MiniMessageComponentI18nFormat;
import xyz.bobkinn.indigoi18n.format.adventure.mixin.LegacyAdventureI18nMixin;
import xyz.bobkinn.indigoi18n.format.adventure.mixin.MiniMessageAdventureI18nMixin;

/**
 * Default adventure i18n instance that adds {@link LegacyAdventureI18nMixin}
 * and {@link MiniMessageAdventureI18nMixin}<br>
 * Its legacy i18n format converts string arguments using specified legacy component serializer and contains
 * default template formatters.<br>
 * Its MiniMessage i18n format also uses default template formatter.<br>
 * Also it supports {@link AdventureFormats#PLAIN} format using
 * {@link ComponentI18nFormat.PlainComponentI18nFormat}
 */
@SuppressWarnings("unused")
@RequiredArgsConstructor
public class AdventureI18n extends StringI18n implements LegacyAdventureI18nMixin, MiniMessageAdventureI18nMixin {
    private final LegacyComponentSerializer legacyComponentSerializer;
    private final MiniMessage miniMessage;

    public static final LegacyComponentSerializer DEFAULT_LEGACY_SERIALIZER = LegacyComponentSerializer.builder()
            .extractUrls().hexColors().character('&').build();

    public static final MiniMessage DEFAULT_MM_SERIALIZER = MiniMessage.miniMessage();

    public AdventureI18n() {
        this(DEFAULT_LEGACY_SERIALIZER, DEFAULT_MM_SERIALIZER);
    }

    @Override
    protected void addDefaultFormats() {
        super.addDefaultFormats(); // add string format
        // add plain format, used as fallback when inlining
        addFormat(AdventureFormats.PLAIN, ComponentI18nFormat.PlainComponentI18nFormat::new);
        addFormat(AdventureFormats.LEGACY,
                c -> new LegacyComponentI18nFormat(c, true, legacyComponentSerializer));
        addFormat(AdventureFormats.MINI_MESSAGE, c -> new MiniMessageComponentI18nFormat(c, miniMessage));
    }
}
