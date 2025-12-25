package xyz.bobkinn.indigoi18n.source;

/**
 * This exception is thrown from translation loading and sources logic.
 */
public class TranslationLoadError extends RuntimeException {
    public TranslationLoadError(String message, Throwable cause) {
        super(message, cause);
    }
}
