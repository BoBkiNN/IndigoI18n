package xyz.bobkinn.indigoi18n.paper;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import xyz.bobkinn.indigoi18n.context.Context;
import xyz.bobkinn.indigoi18n.render.I18nMixin;

public interface CommandSenderLanguageI18nMixin extends I18nMixin {

    /**
     * Returns {@link ViewerLanguageResolver#DEFAULT_RESOLVER} by default.<br>
     * You should override this method if you want to change viewer language lookup behaviour
     * @return viewer language resolver used to extract language from {@link CommandSender viewer}
     */
    default @NotNull ViewerLanguageResolver getViewerLangResolver() {
        return ViewerLanguageResolver.DEFAULT_RESOLVER;
    }

    /**
     * Get language of viewer using {@link #getViewerLangResolver()}
     * @param viewer entity whose language is used
     * @return language of viewer or default language.
     */
    default String getLanguage(CommandSender viewer) {
        return getViewerLangResolver().getLanguage(viewer);
    }

    /**
     * Sets {@link ViewerContext} to context. If context is null, new one is created using {@link #newContext(String, String)}
     * @return context with viewer set
     */
    default Context viewerCtx(Context ctx, CommandSender viewer, String key, String lang) {
        if (ctx == null) return newContext(key, lang).with(new ViewerContext(viewer));
        // override previous viewer context. Guess it is better than keeping any existing.
        return ctx.with(new ViewerContext(viewer));
    }

    /**
     * @return new context with {@link ViewerContext} set
     */
    default Context viewerCtx(CommandSender viewer, String key, String lang) {
        return viewerCtx(null, viewer, key, lang);
    }
}
