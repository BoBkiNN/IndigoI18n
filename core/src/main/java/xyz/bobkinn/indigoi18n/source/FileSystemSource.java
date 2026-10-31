package xyz.bobkinn.indigoi18n.source;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Source which reads languages file from filesystem folder.<br>
 * How it works:<br>
 * 1. List files in folder<br>
 * 2. Filter files by extension<br>
 * 3. Check file validity<br>
 * 4. Extract language id from file name<br>
 * 5. Create source from file and its language<br>
 * 6. Load created source
 * @see TranslationSourceFactory#create(String, Path)
 * @see TranslationSourceProvider.SuffixedFile
 */
@RequiredArgsConstructor
public abstract class FileSystemSource implements MultiLanguageSource {
    private final TranslationSourceProvider provider;

    /**
     * Method used to get path to resolve it in target filesystem.
     * @return folder path relative to filesystem root
     */
    protected abstract Path getFolder();

    /**
     * Method used to get target folder inside filesystem
     * @param fs target filesystem
     * @return Path representing target folder in filesystem
     */
    protected Path getFsFolder(FileSystem fs) {
        var folder = getFolder();
        return fs.getPath(folder.toString());
    }

    /**
     * @return new or existing filesystem
     */
    protected abstract @NotNull FileSystem getFileSystem() throws IOException;

    public Set<Path> listLanguageFiles(FileSystem fs) throws IOException {
        var folder = getFsFolder(fs);
        Set<Path> ret;
        try (var files = Files.list(folder)) {
            ret = files.filter(provider::shouldLoad).collect(Collectors.toSet());
        }
        return ret;
    }

    @Override
    public Set<String> listLanguages() {
        var langNames = new HashSet<String>();
        try (var fs = getFileSystem()) {
            var langFiles = listLanguageFiles(fs);
            for (Path langFile : langFiles) {
                var lang = provider.langFromPath(langFile);
                langNames.add(lang);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to list language files", e);
        }
        return langNames;
    }


    protected void loadLang(FileSystem fs, String language, ISourceTextAdder to) {
        var fn = provider.fileNameForLang(language);
        var path = getFsFolder(fs).resolve(fn);
        if (Files.isDirectory(path)) {
            throw new IllegalArgumentException("Path "+path+" is a folder");
        }
        if (!Files.exists(path)) {
            throw new IllegalArgumentException("Path "+path+" does not exists");
        }
        var s = provider.create(language, path);
        s.load(to);
    }

    @Override
    public void loadLang(String language, ISourceTextAdder to) {
        FileSystem fs;
        try {
            fs = getFileSystem();
        } catch (IOException e) {
            throw new TranslationLoadError("Failed to get file system", e);
        }
        try {
            loadLang(fs, language, to);
        } catch (Exception e) {
            closeSafely(fs, e);
            throw new TranslationLoadError("Failed to load language "+language, e);
        } finally {
            closeSafely(fs, null);
        }
    }

    public void loadFile(@NotNull Path path, ISourceTextAdder to) {
        var lang = provider.langFromPath(path);
        if (!shouldIncludeLanguage(lang)) return;
        var s = provider.create(lang, path);
        s.load(to);
    }

    protected void loadAll(FileSystem fs, ISourceTextAdder to) throws IOException {
        for (var path : listLanguageFiles(fs)) {
            if (Files.isDirectory(path)) {
                throw new IllegalArgumentException("Path "+path+" is a folder");
            }
            if (!Files.exists(path)) {
                throw new IllegalArgumentException("Path "+path+" does not exists");
            }
            loadFile(path, to);
        }
    }

    static void closeSafely(@NotNull FileSystem fs, @Nullable Throwable t) {
        try {
            fs.close();
        } catch (IOException e) {
            if (t != null) t.addSuppressed(e);
            else throw new RuntimeException("Failed to close filesystem", e);
        } catch (UnsupportedOperationException ignored) {
            // some file systems does not support closing, like current system filesystem
        }
    }


    @Override
    public void load(ISourceTextAdder to) {
        FileSystem fs;
        try {
            fs = getFileSystem();
        } catch (IOException e) {
            throw new TranslationLoadError("Failed to get file system", e);
        }
        try {
            loadAll(fs, to);
        } catch (Exception e) {
            closeSafely(fs, e);
            throw new TranslationLoadError("Failed to load all files", e);
        } finally {
            closeSafely(fs, null);
        }
    }

}

