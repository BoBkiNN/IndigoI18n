package xyz.bobkinn.indigoi18n.source.impl;

import xyz.bobkinn.indigoi18n.source.FolderSource;
import xyz.bobkinn.indigoi18n.source.TranslationSource;

import java.io.File;

/**
 * FolderSource to load .properties files from folder using {@link PropertiesSource#fromFile(String, File)}
 * @see FolderSource
 */
public class PropertiesFolderSource extends FolderSource {
    public static final String PROPERTIES_SUFFIX = ".properties";

    public PropertiesFolderSource(String suffix, File folder) {
        super(suffix, folder);
    }

    public PropertiesFolderSource(File folder) {
        this(PROPERTIES_SUFFIX, folder);
    }

    @Override
    public TranslationSource createSource(String language, File file) {
        return PropertiesSource.fromFile(language, file);
    }
}
