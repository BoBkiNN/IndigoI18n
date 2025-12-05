package xyz.bobkinn.indigoi18n;

import xyz.bobkinn.indigoi18n.data.TranslationInfo;
import xyz.bobkinn.indigoi18n.template.TemplateParseException;

import java.util.logging.Level;
import java.util.logging.Logger;

public interface TemplateErrorHandler {
    void handleParseException(TemplateParseException e, String text, TranslationInfo info);

    class JULTemplateErrorHandler implements TemplateErrorHandler {
        public static final Logger LOGGER = Logger.getLogger("IndigoI18n");

        @Override
        public void handleParseException(TemplateParseException e, String text, TranslationInfo info) {
            LOGGER.log(Level.SEVERE, "Failed to parse template '%s' in %s"
                    .formatted(text, info), e);
        }
    }
}
