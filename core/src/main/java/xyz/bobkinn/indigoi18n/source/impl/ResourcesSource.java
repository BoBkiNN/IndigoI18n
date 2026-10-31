package xyz.bobkinn.indigoi18n.source.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.bobkinn.indigoi18n.source.*;

import java.io.IOException;
import java.net.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;


public class ResourcesSource implements MultiLanguageSource {
    private final ClassLoader classLoader;
    private final String folder;
    private final TranslationSourceProvider provider;
    private final URL folderUrl;
    private final URI folderUri;

    private FileSystemSource cachedFsSource;

    public ResourcesSource(ClassLoader classLoader, String folder, TranslationSourceProvider provider) {
        this.classLoader = classLoader;
        if (folder == null || folder.isBlank()) {
            throw new IllegalArgumentException("Folder must not be empty. This is required to list files");
        }
        this.folder = folder;
        this.provider = provider;
        folderUrl = classLoader.getResource(folder);
        if (folderUrl == null) {
            throw new IllegalArgumentException("Cannot find resource folder");
        }
        try {
            folderUri = folderUrl.toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException("Failed to get URI from folder URL", e);
        }
    }

    private FileSystemSource getAsFsSource() {
        var proto = folderUri.getScheme();
        if (proto.equals("file")) {
            var folder = Paths.get(folderUri);
            return new FolderSource(folder, provider);
        } else if (proto.equals("jar")) {
            Path jarPath = getFolderJarPath();
            return new ZipSource(jarPath, folder, provider);
        } else {
            throw new IllegalArgumentException("This folder URI is not supported: "+folderUri);
        }
    }

    private @NotNull Path getFolderJarPath() {
        try {
            if (folderUrl.openConnection() instanceof JarURLConnection conn) {
                var uri = conn.getJarFileURL().toURI();
                return Path.of(uri);
            } else {
                throw new IllegalStateException("Folder URL connection is not JarURLConnection");
            }
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException("Failed to get jar file URI", e);
        }
    }


    @Override
    public Set<String> listLanguages() {
        if (cachedFsSource != null) {
            return cachedFsSource.listLanguages();
        }
        cachedFsSource = getAsFsSource();
        return cachedFsSource.listLanguages();
    }

    private String getResourcePath(String name) {
        if (folder == null || folder.isEmpty()) return name;
        return folder + "/" + name;
    }

    @Override
    public void loadLang(String language, ISourceTextAdder to) {
        var name = provider.fileNameForLang(language);
        var p = getResourcePath(name);
        // we can load single file directly
        var res = classLoader.getResource(p);
        if (res == null) {
            throw new TranslationLoadError("Resource for language "+language+" not found", null);
        }
        URI uri;
        try {
            uri = res.toURI();
        } catch (URISyntaxException e) {
            throw new TranslationLoadError("Failed to get URI from resource URL", e);
        }
        var s = provider.createFromStream(uri, language, () -> {
            try {
                return res.openStream();
            } catch (IOException e) {
                throw new TranslationLoadError("Failed to open resource stream", e);
            }
        });
        s.load(to);
    }

    @Override
    public void load(ISourceTextAdder to) {
        if (cachedFsSource != null) {
            cachedFsSource.load(to);
            return;
        }
        cachedFsSource = getAsFsSource();
        cachedFsSource.load(to);
    }

    @Override
    public @Nullable URI getLocation() {
        return folderUri;
    }
}
