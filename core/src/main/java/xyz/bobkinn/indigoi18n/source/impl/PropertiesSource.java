package xyz.bobkinn.indigoi18n.source.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.bobkinn.indigoi18n.source.ISourceTextAdder;
import xyz.bobkinn.indigoi18n.source.SingleLangSource;
import xyz.bobkinn.indigoi18n.source.TranslationLoadError;
import xyz.bobkinn.indigoi18n.source.TranslationSource;

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * Uses {@link Properties} object to load translations.
 */
@SuppressWarnings("unused")
@RequiredArgsConstructor
public class PropertiesSource implements TranslationSource, SingleLangSource {
    private final URI location;
    @Getter
    private final String language;
    private final Properties properties;

    /**
     * Uses stream to load all properties. Stream remains open.
     */
    @Contract("_, _, _ -> new")
    public static @NotNull PropertiesSource fromStream(URI location, String language, InputStream stream) throws IOException {
        var props = new Properties();
        props.load(stream);
        return new PropertiesSource(location, language, props);
    }

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
            to.add(key, language, v);
        }
    }

    @Override
    public @Nullable URI getLocation() {
        return location;
    }
}
