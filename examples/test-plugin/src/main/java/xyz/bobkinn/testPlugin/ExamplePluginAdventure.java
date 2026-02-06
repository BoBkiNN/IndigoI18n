package xyz.bobkinn.testPlugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.translation.GlobalTranslator;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.bobkinn.indigoi18n.paper.PaperAdventureI18n;
import xyz.bobkinn.indigoi18n.render.adventure.AdventureRenderers;
import xyz.bobkinn.indigoi18n.render.adventure.AdventureTranslator;
import xyz.bobkinn.indigoi18n.source.TranslationLoadError;
import xyz.bobkinn.indigoi18n.source.impl.gson.GsonTranslationSource;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;

@SuppressWarnings("unused")
public class ExamplePluginAdventure extends JavaPlugin {

    // your i18n instance
    private final PaperAdventureI18n i18n;

    // adventure translator which will be used for TranslatableComponent
    private final AdventureTranslator translator;

    public ExamplePluginAdventure() {
        // prepare source to load translations from. Here we will use en2.json file from resources
        GsonTranslationSource source;
        try {
            source = GsonTranslationSource.fromResource("en", getClassLoader(), "en2.json");
        } catch (FileNotFoundException | URISyntaxException e) {
            throw new RuntimeException("Failed to prepare translation source", e);
        }
        // assign new default i18n instance
        i18n = new PaperAdventureI18n();
        // do not forget to run setup()
        i18n.setup();
        // now lets load texts using our translations source
        try {
            //
            i18n.load(source);
        } catch (TranslationLoadError e) {
            throw new RuntimeException("Failed to load translations", e);
        }
        // create translator that will be used for TranslatableComponent
        translator = new AdventureTranslator(
                new NamespacedKey(this, "translator"), // create key for translator
                i18n, // use your I18n instance
                AdventureRenderers.LEGACY // use LEGACY renderer
        );
        // add your translator to global translator
        GlobalTranslator.translator().addSource(translator);
    }

    @Override
    public void onEnable() {
        // broadcast example_plugin.enabled message in english
        Component msg = i18n.parseA("en", "example_plugin.enabled");
        Bukkit.broadcast(msg);
    }

    @Override
    public void onDisable() {
        // remove adventure translator instance from global translator
        GlobalTranslator.translator().removeSource(translator);
        // unload texts
        i18n.unloadAll();
    }

}
