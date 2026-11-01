package xyz.bobkinn.indigoi18n.source.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.bobkinn.indigoi18n.source.FileSystemSource;
import xyz.bobkinn.indigoi18n.source.TranslationSourceProvider;

import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.Path;

public class FolderSource extends FileSystemSource {
    private final Path folder;

    public FolderSource(Path folder, TranslationSourceProvider provider) {
        super(provider);
        this.folder = folder;
    }

    @Override
    public Path getFolder() {
        return folder;
    }

    @Override
    protected Path getFsFolder(FileSystem fs) {
        return folder;
    }

    @Override
    protected @NotNull FileSystem getFileSystem() {
        return folder.getFileSystem();
    }

    @Override
    protected boolean shouldCloseFileSystem(FileSystem fs) {
        return false;
    }

    @Override
    public @Nullable URI getLocation() {
        return folder.toUri();
    }
}
