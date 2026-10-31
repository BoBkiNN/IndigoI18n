package xyz.bobkinn.indigoi18n.source;

import java.nio.file.Path;

/**
 * @see #create(String, Path) 
 */
@FunctionalInterface
public interface TranslationSourceFactory {
    /**
     * Method used to create temporary source to load translations from
     * @param language language id of texts in target path
     * @param path path to use
     * @return new source
     */
    TranslationSource create(String language, Path path);
}
