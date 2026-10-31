package xyz.bobkinn.indigoi18n.source;

import java.io.InputStream;
import java.net.URI;
import java.nio.file.Path;
import java.util.function.Supplier;

/**
 * @see #createFromPath(String, Path)
 */
public interface TranslationSourceFactory {
    /**
     * Method used to create temporary source to load translations from
     * @param language language id of texts in target path
     * @param path path to use
     * @return new source
     */
    TranslationSource createFromPath(String language, Path path);

    /**
     * Method used to create temporary source to load translation from.
     * @param stream stream supplier. Must be closed manually
     */
    TranslationSource createFromStream(URI location, String language, Supplier<InputStream> stream);
}
