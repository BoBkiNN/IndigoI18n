package xyz.bobkinn.indigoi18n;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import xyz.bobkinn.indigoi18n.codegen.GenStaticDefault;
import xyz.bobkinn.indigoi18n.render.RenderType;
import xyz.bobkinn.indigoi18n.render.impl.StringRenderer;
import xyz.bobkinn.indigoi18n.render.impl.StringI18nMixin;


/**
 * Simple I18n class. It uses only {@link StringRenderer} renderer and implements {@link StringI18nMixin}.
 * @see StringRenderer
 */
@GenStaticDefault(name = "Indigo", creator = "create")
public class StringI18n extends IndigoI18n implements StringI18nMixin {

    @Contract(" -> new")
    public static @NotNull StringI18n create() {
        var r = new StringI18n();
        r.setup();
        return r;
    }

    @Override
    protected void addDefaultRenderers() {
        addRenderer(RenderType.STRING, StringRenderer::new);
    }
}
