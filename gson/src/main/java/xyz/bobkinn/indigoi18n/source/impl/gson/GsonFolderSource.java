package xyz.bobkinn.indigoi18n.source.impl.gson;

import xyz.bobkinn.indigoi18n.source.FolderSource;
import xyz.bobkinn.indigoi18n.source.TranslationSource;
import xyz.bobkinn.indigoi18n.source.impl.PropertiesSource;

import java.io.File;
import java.net.URI;

/**
 * FolderSource to load .json files from folder using {@link GsonTranslationSource#fromFile(String, File)}
 * @see FolderSource
 */
public class GsonFolderSource extends FolderSource {
    public static final String JSON_SUFFIX = ".json";

    public GsonFolderSource(String suffix, File folder) {
        super(suffix, folder);
    }

    public GsonFolderSource(File folder) {
        this(JSON_SUFFIX, folder);
    }

    @Override
    public TranslationSource createSource(String language, File file) {
        return GsonTranslationSource.fromFile(language, file);
    }
}
