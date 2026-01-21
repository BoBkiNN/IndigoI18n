package xyz.bobkinn.indigoi18n.paper;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import xyz.bobkinn.indigoi18n.context.Context;
import xyz.bobkinn.indigoi18n.format.adventure.AdventureFormats;
import xyz.bobkinn.indigoi18n.format.adventure.AdventureI18n;
import xyz.bobkinn.indigoi18n.format.adventure.ComponentTemplateFormatter;
import xyz.bobkinn.indigoi18n.format.adventure.format.ComponentI18nFormat;
import xyz.bobkinn.indigoi18n.template.format.FormatPattern;

/**
 * Same as {@link AdventureI18n} but also with 3 mixins using {@link CommandSenderLanguageI18nMixin}
 * @see AdventureI18n
 */
@SuppressWarnings("unused")
public class PaperAdventureI18N extends AdventureI18n implements MiniMessageAdventurePaperI18nMixin,
        LegacyAdventurePaperI18nMixin, StringPaperI18nMixin {
    public PaperAdventureI18N() {
        super();
    }

    public PaperAdventureI18N(LegacyComponentSerializer serializer, MiniMessage miniMessage) {
        super(serializer, miniMessage);
    }

    /**
     * Argument converter for {@link Entity} that creates text with entity name and attaches hover event by
     * {@link Entity#asHoverEvent()}.<br>
     * {@link FormatPattern} is applied to entity name text if this instance has {@link ComponentI18nFormat}
     * {@link AdventureFormats#PLAIN plain format} and String converter
     * is {@link ComponentTemplateFormatter#findConverter(Object) found}
     */
    public Component convertEntity(Context ctx, @NotNull Entity e, FormatPattern format) {
        var eName = e.getName();
        var f = getFormat(AdventureFormats.PLAIN);
        Component base = null;
        if (f instanceof ComponentI18nFormat cf) {
            var conv = cf.getTemplateFormatter().findConverter(eName);
            if (conv != null) base = conv.format(ctx, eName, format);
        }
        var hs = e.asHoverEvent();
        if (base != null) {
            return base.hoverEvent(hs);
        } else return Component.text(eName).hoverEvent(hs);
    }

    @Override
    public void setup() {
        super.setup();
        // register custom converters once formats added
        addPaperConverters();
    }

    protected void addPaperConverters() {
        // add entity converter
        putConverter(Entity.class, this::convertEntity);
    }
}
