package xyz.bobkinn.indigoi18n;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import xyz.bobkinn.indigoi18n.data.TemplateCache;
import xyz.bobkinn.indigoi18n.source.impl.FsFolderSource;
import xyz.bobkinn.indigoi18n.source.impl.JarFolderSource;
import xyz.bobkinn.indigoi18n.source.impl.PropertiesSource;
import xyz.bobkinn.indigoi18n.template.TemplateParser;

import java.net.URISyntaxException;
import java.nio.file.Path;

public class TestFsSource {

    private @NotNull Path getResourcePath(String res) {
        var url = getClass().getClassLoader().getResource(res);
        Assertions.assertNotNull(url);
        try {
            return Path.of(url.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testFsFolder() {
        var folder = getResourcePath("test_prop");
        var tr = new Translations(new TemplateCache(TemplateParser.INSTANCE));
        var s = new FsFolderSource(folder, PropertiesSource.PROVIDER);
        tr.load(s);
        assert tr.has("item.apple");
    }

    @Test
    public void testZip() {
        var zip = getResourcePath("test_prop.zip");
        var tr = new Translations(new TemplateCache(TemplateParser.INSTANCE));
        var s = new JarFolderSource(zip, "", PropertiesSource.PROVIDER);
        tr.load(s);
        assert tr.has("item.apple");
    }

    @Test
    public void testZipFolder() {
        var zip = getResourcePath("test_prop_folder.zip");
        var tr = new Translations(new TemplateCache(TemplateParser.INSTANCE));
        var s = new JarFolderSource(zip, "lang", PropertiesSource.PROVIDER);
        tr.load(s);
        assert tr.has("item.apple");
    }
}
