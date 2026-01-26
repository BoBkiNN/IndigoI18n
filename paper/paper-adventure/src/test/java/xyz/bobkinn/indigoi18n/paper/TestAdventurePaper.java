package xyz.bobkinn.indigoi18n.paper;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import net.kyori.adventure.text.Component;
import org.bukkit.event.player.PlayerQuitEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestAdventurePaper {

    private ServerMock server;
    private MyTestPlugin plugin;

    @BeforeEach
    public void setUp() {
        // Start the mock server
        server = MockBukkit.mock();
        plugin = MockBukkit.load(MyTestPlugin.class);
    }

    @AfterEach
    public void tearDown() {
        // Stop the mock server
        MockBukkit.unmock();
    }

    @Test
    public void test() {
        var v  = new MyPlayerMock(server, "jeb_");
        server.addPlayer(v);
        server.addPlayer(new MyPlayerMock(server, "Notch")); // player join
        plugin.onPlayerQuit(new PlayerQuitEvent(v, (Component) null, PlayerQuitEvent.QuitReason.DISCONNECTED));
    }

}
