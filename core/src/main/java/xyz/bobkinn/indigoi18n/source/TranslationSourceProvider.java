package xyz.bobkinn.indigoi18n.source;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;


/**
 * Interface that provides rules and actions for {@link FileSystemSource} to work with multiple files
 */
public interface TranslationSourceProvider extends TranslationSourceFactory {

    /**
     * @param path path to load in target filesystem.
     * @return true if file at this path should be loaded by target translation loader
     */
    boolean shouldLoad(Path path);

    /**
     * Construct file name for language id
     * @param lang language id
     * @return file name
     */
    String fileNameForLang(String lang);

    /**
     * Get language ID from file path.
     * By default, returns file name without extension
     */
    default @NotNull String langFromPath(@NotNull Path path) {
        var fileName = path.getFileName().toString();
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? fileName : fileName.substring(0, dotIndex);
    }

    /**
     * Source representing single file with extension
     */
    @RequiredArgsConstructor
    @Getter
    class SuffixedFile implements TranslationSourceProvider {
        private final String suffix;
        private final TranslationSourceFactory factory;

        @Override
        public boolean shouldLoad(Path path) {
            return Files.isRegularFile(path) && path.getFileName().toString().endsWith(suffix);
        }

        @Override
        public String fileNameForLang(String lang) {
            return lang+suffix;
        }

        @Override
        public TranslationSource create(String language, Path path) {
            return factory.create(language, path);
        }
    }
}
