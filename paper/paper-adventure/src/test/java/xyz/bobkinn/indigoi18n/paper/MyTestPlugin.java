package xyz.bobkinn.indigoi18n.paper;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.translation.GlobalTranslator;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.jetbrains.annotations.NotNull;
import xyz.bobkinn.indigoi18n.render.adventure.AdventureRenderers;
import xyz.bobkinn.indigoi18n.render.adventure.AdventureTranslator;
import xyz.bobkinn.indigoi18n.source.TranslationLoadError;
import xyz.bobkinn.indigoi18n.source.impl.gson.GsonTranslationSource;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;

public class MyTestPlugin extends JavaPlugin implements Listener {

    // your i18n instance
    private final PaperAdventureI18n i18n;
    // translation source will be loading
    private final GsonTranslationSource source;

    // adventure translator which will be used for TranslatableComponent
    private final AdventureTranslator translator;

    public MyTestPlugin(@NotNull JavaPluginLoader loader, @NotNull PluginDescriptionFile description, @NotNull File dataFolder, @NotNull File file) {
        super(loader, description, dataFolder, file);
        // prepare source to load translations from. Here we will use en.json file from resources
        try {
            source = GsonTranslationSource.fromResource("en", getClassLoader(), "en.json");
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

    /**
     * Event to handle player joins. Uses i18n instance directly with single language
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        var player = e.getPlayer();
        // get message. Here we will use "en" language and key "myplugin.message.join". Also, we will pass player as argument.
        // player argument will be converted to its name with hover displaying entity info
        var msg = i18n.parseA("en", "myplugin.message.join", player);
        e.joinMessage(msg); // set join message
    }

    /**
     * Event to handle player joins. Uses adventure translator
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        var player = e.getPlayer();
        // Create argument that will be inserting into text.
        // TranslatableComponent does not allow any objects to be passed as arguments so we must create component ourselves.
        // (but it is still possible to implement custom adventure TranslationArgument to pass any objects)
        var arg = player.name().hoverEvent(player.asHoverEvent());

        // Instead of getting same message for all viewers, we will create TranslatableComponent.
        // Keys here should be unique across different global translator sources and builtin minecraft texts.
        // Because of this keys are prefixed with plugin name
        var msg = Component.translatable("myplugin.message.quit")
                .arguments(arg); // set arguments
        e.quitMessage(msg); // set quit message
        // now, different players will see message from our texts based on their language.
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // remove adventure translator instance from global translator
        GlobalTranslator.translator().removeSource(translator);
        // unload texts
        i18n.unload(source);
    }
}
