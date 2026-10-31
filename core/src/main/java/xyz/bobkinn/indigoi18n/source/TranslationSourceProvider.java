package xyz.bobkinn.indigoi18n.source;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.file.Files;
import java.nio.file.Path;


public interface TranslationSourceProvider extends TranslationSourceFactory {

    /**
     * @param path path to load in target filesystem.
     * @return true if file at this path should be loaded by target translation loader
     */
    boolean shouldLoad(Path path);

    /**
     * Construct file name from language
     * @param lang language id
     * @return file name
     */
    String fileNameFromLang(String lang);

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
        public String fileNameFromLang(String lang) {
            return lang+suffix;
        }

        @Override
        public TranslationSource create(String language, Path path) {
            return factory.create(language, path);
        }
    }
}
