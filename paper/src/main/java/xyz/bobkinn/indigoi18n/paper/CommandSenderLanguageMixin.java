package xyz.bobkinn.indigoi18n.paper;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import xyz.bobkinn.indigoi18n.context.Context;

public interface CommandSenderLanguageMixin {

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

    default Context injectCtx(Context ctx, CommandSender viewer) {
        if (ctx == null) return new Context().with(new ViewerContext(viewer));
        // override previous viewer context. Guess it is better than keeping any existing.
        return ctx.with(new ViewerContext(viewer));
    }

    default Context viewerCtx(CommandSender viewer) {
        return new Context().with(new ViewerContext(viewer));
    }
}
