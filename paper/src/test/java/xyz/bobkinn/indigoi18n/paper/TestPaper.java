package xyz.bobkinn.indigoi18n.paper;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import xyz.bobkinn.indigoi18n.StringI18n;
import xyz.bobkinn.indigoi18n.data.BasicTranslation;
import xyz.bobkinn.indigoi18n.format.RenderType;
import xyz.bobkinn.indigoi18n.format.impl.StringRenderer;

public class TestPaper {

    static class TestPaperI18N extends StringI18n implements StringPaperI18nMixin {

    }

    record ViewerName() {}

    private ServerMock server;

    @BeforeEach
    public void setUp() {
        // Start the mock server
        server = MockBukkit.mock();
    }

    @AfterEach
    public void tearDown() {
        // Stop the mock server
        MockBukkit.unmock();
    }

    @Setter
    static class LocalizedPlayerMock extends PlayerMock {
        private String locale = "en_us";

        public LocalizedPlayerMock(ServerMock server, String name) {
            super(server, name);
        }

        @Override
        public @NotNull String getLocale() {
            return locale;
        }

    }

    @Test
    void testViewer() {
        var i18n = new TestPaperI18N();
        i18n.setup();
        var r = (StringRenderer) i18n.getRenderer(RenderType.STRING);
        // add marker handling
        r.getTemplateFormatter().putConverter(ViewerName.class, (ctx, argument, format) -> {
            var viewer = ctx.getOptional(ViewerContext.class, ViewerContext::getViewer).orElseThrow();
            return viewer.getName();
        });
        var viewer = new LocalizedPlayerMock(server, "Meow");
        viewer.setLocale("en");
        server.addPlayer(viewer);
        i18n.getTexts().put("test", "en", new BasicTranslation("Viewer name is %s"));
        var res = i18n.parse(viewer, "test", new ViewerName());
        Assertions.assertEquals("Viewer name is Meow", res);
    }
}
