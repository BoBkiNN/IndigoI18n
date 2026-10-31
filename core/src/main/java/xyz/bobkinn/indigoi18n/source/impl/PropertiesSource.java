package xyz.bobkinn.indigoi18n.source.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.bobkinn.indigoi18n.data.Translation;
import xyz.bobkinn.indigoi18n.source.*;

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Uses {@link Properties} object to load translations.
 */
@SuppressWarnings("unused")
@RequiredArgsConstructor
public class PropertiesSource implements TranslationSource, SingleLangSource {

    public static final String FILE_SUFFIX = ".properties";
    public static final TranslationSourceProvider.SuffixedFile PROVIDER =
            new TranslationSourceProvider.SuffixedFile(FILE_SUFFIX, PropertiesSource::fromPath);

    private final URI location;
    @Getter
    private final String language;
    private final Properties properties;

    /**
     * Creates new {@link FolderSource} from `.properties` files in folder
     * @param folder folder with {@code <lang>.properties} files
     * @return new folder source
     */
    @Contract("_ -> new")
    public static @NotNull FolderSource fromFiles(Path folder) {
        return new FolderSource(folder, PROVIDER);
    }

    /**
     * Creates new {@link ZipSource} from `.properties` files in zip located in {@code folder} folder inside zip file
     * @param zip zip with {@code <lang>.properties} files'
     * @param folder folder path inside zip where to list files.
     * @return new zip source
     */
    public static @NotNull ZipSource fromZip(Path zip, @NotNull String folder) {
        return new ZipSource(zip, folder, PROVIDER);
    }

    /**
     * Uses stream to load all properties. Stream remains open.
     */
    @Contract("_, _, _ -> new")
    public static @NotNull PropertiesSource fromStream(URI location, String language, InputStream stream) throws IOException {
        var props = new Properties();
        props.load(stream);
        return new PropertiesSource(location, language, props);
    }

    /**
     * Uses {@link StandardCharsets#UTF_8} encoding to read entire file and create {@link Properties} to use in new source.<br>
     * File must be saved with UTF-8 encoding to allow any language to be written and read correctly without escaping.
     * @param language language id
     * @param path path to read
     * @return new source
     */
    @Contract("_, _ -> new")
    public static @NotNull PropertiesSource fromPath(String language, Path path) {
        if (!Files.isRegularFile(path)) {
            throw new TranslationLoadError("Path is not a regular file", null);
        }
        try (var reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            var props = new Properties();
            props.load(reader);
            return new PropertiesSource(path.toUri(), language, props);
        } catch (FileNotFoundException e) {
            throw new TranslationLoadError("No file found to load translations from", e);
        } catch (IOException e) {
            throw new TranslationLoadError("Failed to load properties file", e);
        }
    }

    /**
     * Uses {@link StandardCharsets#UTF_8} encoding to read entire file and create {@link Properties} to use in new source.<br>
     * File must be saved with UTF-8 encoding to allow any language to be written and read correctly without escaping.
     * @param language language id
     * @param file file to load from
     * @return new source
     */
    public static PropertiesSource fromFile(String language, File file) {
        try (var reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            var props = new Properties();
            props.load(reader);
            return new PropertiesSource(file.toURI(), language, props);
        } catch (FileNotFoundException e) {
            throw new TranslationLoadError("No file found to load translations from", e);
        } catch (IOException e) {
            throw new TranslationLoadError("Failed to load properties file", e);
        }
    }

    @Override
    public void load(ISourceTextAdder to) {
        var keys = properties.stringPropertyNames();
        for (String key : keys) {
            var v = properties.getProperty(key);
            to.add(key, language, Translation.create(v));
        }
    }

    @Override
    public @Nullable URI getLocation() {
        return location;
    }
}
