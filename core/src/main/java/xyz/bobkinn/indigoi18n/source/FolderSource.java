package xyz.bobkinn.indigoi18n.source;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

/**
 * Source which reads languages file from folder.<br>
 * How it works:<br>
 * 1. List files in folder<br>
 * 2. Filter files by extension<br>
 * 3. Check file validity<br>
 * 4. Extract language id from file name<br>
 * 5. Create source from file and its language<br>
 * 6. Load created source
 * @see #createSource(String, File)
 */
@RequiredArgsConstructor
public abstract class FolderSource implements MultiLanguageSource {
    private final String suffix;
    private final File folder;

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    protected boolean isValidFileName(String fileName) {
        return fileName.endsWith(suffix);
    }

    public Set<File> listLanguageFiles() {
        var ls = folder.listFiles();
        if (ls == null) throw new IllegalArgumentException("Failed to list folder "+folder);
        var ret = new HashSet<File>();
        for (var f : ls) {
            if (f.isDirectory()) continue;
            if (!isValidFileName(f.getName())) continue;
            ret.add(f);
        }
        return ret;
    }

    @Override
    public Set<String> listLanguages() {
        var ls = folder.list();
        if (ls == null) throw new IllegalArgumentException("Failed to list folder "+folder);
        var ret = new HashSet<String>();
        for (String f : ls) {
            if (!isValidFileName(f)) continue;
            ret.add(MultiLanguageSource.langFromFileName(f));
        }
        return ret;
    }

    protected String fileNameFromLang(String lang) {
        return lang+suffix;
    }

    /**
     * Method used to create temporary source to load translations from
     * @param language language id of texts in target file
     * @param file file to use
     * @return new source
     */
    public abstract TranslationSource createSource(String language, File file);

    @Override
    public void loadLang(String language, ISourceTextAdder to) {
        var fn = fileNameFromLang(language);
        var file = new File(folder, fn);
        if (file.isDirectory()) {
            throw new IllegalArgumentException("File "+file+" is a folder");
        }
        if (!file.exists()) {
            throw new IllegalArgumentException("FIle "+file+" does not exists");
        }
        var s = createSource(language, file);
        s.load(to);
    }

    public void loadFile(@NotNull File file, ISourceTextAdder to) {
        var lang = MultiLanguageSource.langFromFileName(file.getName());
        if (!shouldIncludeLanguage(lang)) return;
        var s = createSource(lang, file);
        s.load(to);
    }

    @Override
    public void load(ISourceTextAdder to) {
        for (var file : listLanguageFiles()) {
            if (file.isDirectory()) {
                throw new IllegalArgumentException("File "+file+" is a folder");
            }
            if (!file.exists()) {
                throw new IllegalArgumentException("FIle "+file+" does not exists");
            }
            loadFile(file, to);
        }
    }

    @Override
    public @Nullable URI getLocation() {
        return folder.toURI();
    }
}

