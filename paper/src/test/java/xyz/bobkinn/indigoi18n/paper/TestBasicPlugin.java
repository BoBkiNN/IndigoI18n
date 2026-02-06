package xyz.bobkinn.indigoi18n.paper;

import be.seeseemelk.mockbukkit.MockBukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.junit.jupiter.api.Test;

public class TestBasicPlugin {
    @Test
    void test() {
        var server = MockBukkit.mock();
        var p = server.addPlayer();
        var pdf = new PluginDescriptionFile("ExamplePluginBasic",
                "1.0.0", "xyz.bobkinn.indigoi18n.paper.TestBasicPlugin");
        var pl = server.getPluginManager().loadPlugin(ExamplePluginBasic.class, pdf, new Object[0]);
        server.getPluginManager().enablePlugin(pl);
        p.assertSaid("Plugin enabled!");
        MockBukkit.unmock();
    }
}
