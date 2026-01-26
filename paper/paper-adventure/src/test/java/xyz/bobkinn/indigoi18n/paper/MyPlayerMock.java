package xyz.bobkinn.indigoi18n.paper;

import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import org.jetbrains.annotations.NotNull;

public class MyPlayerMock extends PlayerMock {
    public MyPlayerMock(ServerMock server, String name) {
        super(server, name);
    }

    @Override
    public @NotNull Component name() {
        return Component.text(getName());
    }

    @Override
    public @NotNull HoverEvent<HoverEvent.ShowEntity> asHoverEvent() {
        return HoverEvent.showEntity(getType().key(), getUniqueId());
    }
}
