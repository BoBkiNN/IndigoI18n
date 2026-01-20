package xyz.bobkinn.indigoi18n;

import net.xyzsd.plurals.PluralCategory;
import org.junit.jupiter.api.Test;
import xyz.bobkinn.indigoi18n.context.Context;
import xyz.bobkinn.indigoi18n.context.impl.CountContext;
import xyz.bobkinn.indigoi18n.data.PluralTranslation;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestPlurals {

    @Test
    public void test() {
        var i18n = new StringI18n();
        i18n.setup();
        i18n.texts.put("a", "ru", new PluralTranslation(Map.of(
                PluralCategory.ONE, "одно яблоко",
                PluralCategory.FEW, "%s яблока",
                PluralCategory.MANY, "%s яблок",
                PluralCategory.OTHER, "%s яблока"
        )));
        var t = i18n.parse(new Context().with(new CountContext(3)), "ru", "a", 3);
        assertEquals("3 яблока", t);
        var t2 = i18n.parse(new Context().with(new CountContext(5)), "ru", "a", 5);
        assertEquals("5 яблок", t2);
    }
}
