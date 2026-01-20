package xyz.bobkinn.indigoi18n.format.adventure;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.translation.GlobalTranslator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import xyz.bobkinn.indigoi18n.data.BasicTranslation;

import java.util.Locale;

public class TestTranslator {

    @Test
    void testTranslator() {
        var i18n = new AdventureI18n();
        var translator = new AdventureTranslator(Key.key("test:test"), i18n, AdventureFormats.LEGACY);
        GlobalTranslator.translator().addSource(translator);
        i18n.getTexts().put("test", "en", BasicTranslation.create("&cRed"));
        var r = GlobalTranslator.render(Component.translatable("test"), Locale.ENGLISH);
        Assertions.assertEquals(Component.empty()
                .color(NamedTextColor.RED)
                .append(Component.text("Red")), r);
    }
}
