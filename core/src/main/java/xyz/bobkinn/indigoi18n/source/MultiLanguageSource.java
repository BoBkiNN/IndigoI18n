package xyz.bobkinn.indigoi18n.source;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Translation source which loads translations using separate entities per languages, for example different files
 */
public interface MultiLanguageSource extends TranslationSource {

    /**
     * Returns list of language ids that can be loaded
     */
    Set<String> listLanguages();

    /**
     * @param lang language id
     * @return false to skip language
     */
    @SuppressWarnings("unused")
    default boolean shouldIncludeLanguage(String lang) {
        return true;
    }

    /**
     * Load language texts by language id
     * @param language language id
     * @param to target
     */
    void loadLang(String language, ISourceTextAdder to);

    @Override
    default void load(ISourceTextAdder to) {
        for (var lang : listLanguages()) {
            if (!shouldIncludeLanguage(lang)) continue;
            loadLang(lang, to);
        }
    }

    /**
     * Returns file name without last extension
     */
    static @NotNull String langFromFileName(@NotNull String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? fileName : fileName.substring(0, dotIndex);
    }
}
