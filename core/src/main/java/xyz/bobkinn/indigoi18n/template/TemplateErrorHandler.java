package xyz.bobkinn.indigoi18n.template;

import xyz.bobkinn.indigoi18n.context.Context;

import java.util.logging.Level;
import java.util.logging.Logger;

public interface TemplateErrorHandler {
    void handleParseException(TemplateParseException e, String text, Context ctx);

    class JULTemplateErrorHandler implements TemplateErrorHandler {
        public static final Logger LOGGER = Logger.getLogger("IndigoI18n");

        @Override
        public void handleParseException(TemplateParseException e, String text, Context ctx) {
            var info = ctx.collectInfo();
            LOGGER.log(Level.SEVERE, "Failed to parse template '%s' in %s"
                    .formatted(text, info), e);
        }
    }
}
