package xyz.bobkinn.indigoi18n;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import xyz.bobkinn.indigoi18n.data.BasicTranslation;
import xyz.bobkinn.indigoi18n.source.SourceTextAdder;
import xyz.bobkinn.indigoi18n.source.impl.PropertiesFolderSource;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Map;

public class TestPropertiesFolder {

    static class TestAdder extends SourceTextAdder {

        public TestAdder() {
            super((k, l, t) -> {});
        }

        public String get(String key, String language) {
            var t  = getAdded().get(Map.entry(key, language));
            return ((BasicTranslation) t).getText();
        }
    }

    @Test
    void test() throws URISyntaxException {
        var url = getClass().getClassLoader().getResource("test_prop");
        if (url == null) throw new IllegalArgumentException("No folder found");
        var folder = Paths.get(url.toURI()).toFile();
        var source = new PropertiesFolderSource(folder);
        var adder = new TestAdder();
        source.load(adder);
        Assertions.assertEquals("Apple", adder.get("item.apple", "en_us"));
        Assertions.assertEquals("Яблоко", adder.get("item.apple", "ru_ru"));
    }
}
