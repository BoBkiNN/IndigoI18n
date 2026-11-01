package xyz.bobkinn.indigoi18n.source.impl;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.bobkinn.indigoi18n.source.FileSystemSource;
import xyz.bobkinn.indigoi18n.source.TranslationSourceProvider;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Map;


/**
 * Source that uses JAR or ZIP file to list translations
 */
public class ZipSource extends FileSystemSource {
    @Getter
    private final Path jar;
    private final Path folder;
    private final URI withEntrypoint;
    private final URI jarFsUri;

    /**
     *
     * @param jar path to jar file in current filesystem
     * @param folder folder inside of archive. If empty string, archive root is used
     */
    public ZipSource(Path jar, @NotNull String folder, TranslationSourceProvider provider) {
        super(provider);
        this.jar = jar;
        this.folder = Path.of(folder);
        var jarUri = jar.toUri();
        this.jarFsUri = getJarUri(jarUri, null);
        this.withEntrypoint = getJarUri(jarUri, folder);
    }

    private static URI getJarUri(URI jar, String entry) {
        // jar: works with zip too
        var sb = new StringBuilder("jar:");
        sb.append(jar.toString());
        if (entry != null && !entry.isEmpty()) {
            sb.append("!/").append(entry);
        }
        return URI.create(sb.toString());
    }

    @Override
    protected Path getFolder() {
        return folder;
    }

    @Override
    protected @NotNull FileSystem getFileSystem() throws IOException {
        return FileSystems.newFileSystem(jarFsUri, Map.of());
    }

    @Override
    protected boolean shouldCloseFileSystem(FileSystem fs) {
        return true;
    }

    @Override
    public @Nullable URI getLocation() {
        return withEntrypoint;
    }
}
