package xyz.bobkinn.indigoi18n.paper;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.bobkinn.indigoi18n.Indigo;
import xyz.bobkinn.indigoi18n.source.TranslationLoadError;
import xyz.bobkinn.indigoi18n.source.TranslationSource;
import xyz.bobkinn.indigoi18n.source.impl.gson.GsonTranslationSource;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;

/**
 * Here we will be using global {@link xyz.bobkinn.indigoi18n.StringI18n} instance {@link Indigo} to broadcast message
 */
public class ExamplePluginBasic extends JavaPlugin {
    private final TranslationSource source;

    public ExamplePluginBasic() {
        // prepare source to load translations from. Here we will use en.json file from resources
        try {
            source = GsonTranslationSource.fromResource("en", getClassLoader(), "en.json");
        } catch (FileNotFoundException | URISyntaxException e) {
            throw new RuntimeException("Failed to prepare translation source", e);
        }
        // now lets load texts using our translations source
        try {
            // load new text to global instance
            Indigo.INSTANCE.load(source);
        } catch (TranslationLoadError e) {
            throw new RuntimeException("Failed to load translations", e);
        }
    }

    @Override
    public void onEnable() {
        // broadcast example_plugin.enabled message in english
        String msg = Indigo.parse("en", "example_plugin.enabled");
        //noinspection deprecation
        Bukkit.broadcastMessage(msg);
    }

    @Override
    public void onDisable() {
        // unload our source to keep global instance clear
        Indigo.INSTANCE.unload(source);
    }

}
