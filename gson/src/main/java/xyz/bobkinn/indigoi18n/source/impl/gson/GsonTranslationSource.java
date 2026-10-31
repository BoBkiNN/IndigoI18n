package xyz.bobkinn.indigoi18n.source.impl.gson;

import com.google.gson.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.bobkinn.indigoi18n.data.BasicTranslation;
import xyz.bobkinn.indigoi18n.data.PluralTranslation;
import xyz.bobkinn.indigoi18n.data.Translation;
import xyz.bobkinn.indigoi18n.source.*;
import xyz.bobkinn.indigoi18n.source.impl.FolderSource;
import xyz.bobkinn.indigoi18n.source.impl.ZipSource;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class GsonTranslationSource implements TranslationSource, SingleLangSource {
    private final @Nullable URI location;
    private final Function<Gson, JsonElement> jsonSupplier;
    private final Function<ContextParser, Gson> gsonSupplier;
    @Getter
    private final String language;
    private final ContextParser contextParser;

    public static ContextParser DEFAULT_CONTEXT_PARSER = new ContextParser();

    @SuppressWarnings("unused")
    public static TranslationSourceProvider PROVIDER =
            new TranslationSourceProvider.SuffixedFile(".json", new TranslationSourceFactory() {
                @Override
                public TranslationSource createFromPath(String language, Path path) {
                    return GsonTranslationSource.fromPath(language, path);
                }

                @Override
                public TranslationSource createFromStream(URI location, String language, Supplier<InputStream> stream) {
                    return GsonTranslationSource.fromStream(location, language, stream);
                }
            });

    /**
     * Creates new {@link FolderSource} from `.json` files in folder
     * @param folder folder with {@code <lang>.json} files
     * @return new folder source
     */
    @Contract("_ -> new")
    public static @NotNull FolderSource fromFiles(Path folder) {
        return new FolderSource(folder, PROVIDER);
    }

    /**
     * Creates new {@link ZipSource} from `.json` files in zip located in {@code folder} folder inside zip file
     * @param zip zip with {@code <lang>.json} files'
     * @param folder folder path inside zip where to list files.
     * @return new zip source
     */
    @SuppressWarnings("unused")
    public static @NotNull ZipSource fromZip(Path zip, @NotNull String folder) {
        return new ZipSource(zip, folder, PROVIDER);
    }

    public static final Map<String, Class<? extends Translation>> TRANSLATION_TYPES = Map.of(
            "basic", BasicTranslation.class,
            "plural", PluralTranslation.class
    ) ;

    private static final Function<ContextParser, Gson> DEFAULT_GSON_SUPPLIER = (p) -> new GsonBuilder()
            .registerTypeAdapter(Translation.class, DiscriminatorAdapter.mapping(TRANSLATION_TYPES))
            .registerTypeAdapter(PluralTranslation.class, new PluralTranslationAdapter(p))
            .registerTypeAdapter(BasicTranslation.class, new BasicTranslationAdapter(p))
            .create();

    @Contract("_, _, _ -> new")
    public static @NotNull GsonTranslationSource fromElement(@Nullable URI location, String language, JsonElement element) {
        return new GsonTranslationSource(location, (g) -> element, DEFAULT_GSON_SUPPLIER, language, DEFAULT_CONTEXT_PARSER);
    }

    @Contract("_, _ -> new")
    public static @NotNull GsonTranslationSource fromFile(String language, File file) {
        return new GsonTranslationSource(file.toURI(), (g) -> {
            try (var fr = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
                return g.fromJson(fr, JsonObject.class);
            } catch (IOException e) {
                throw new TranslationLoadError("IO exception when reading json object", e);
            }
        }, DEFAULT_GSON_SUPPLIER, language, DEFAULT_CONTEXT_PARSER);
    }

    @Contract("_, _ -> new")
    public static @NotNull GsonTranslationSource fromPath(String language, Path path) {
        if (!Files.isRegularFile(path)) {
            throw new TranslationLoadError("Path is not a regular file", null);
        }
        return new GsonTranslationSource(path.toUri(), (g) -> {
            try (var fr = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                return g.fromJson(fr, JsonObject.class);
            } catch (IOException e) {
                throw new TranslationLoadError("IO exception when reading json object", e);
            }
        }, DEFAULT_GSON_SUPPLIER, language, DEFAULT_CONTEXT_PARSER);
    }

    /**
     * Creates new source which will load json from resource input stream.
     * Resource must exist at call time to access its URL.
     * @param language id of language which this source is loading
     * @param stream resource stream
     */
    @Contract("_, _, _ -> new")
    public static @NotNull GsonTranslationSource fromStream(URI location, String language, Supplier<InputStream> stream) {
        return new GsonTranslationSource(location, (g) -> {
            var is = stream.get();
            if (is == null) throw new IllegalArgumentException("Resource stream not found");
            try (var fr = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                return g.fromJson(fr, JsonObject.class);
            } catch (IOException e) {
                throw new TranslationLoadError("IO exception when reading json object", e);
            }
        }, DEFAULT_GSON_SUPPLIER, language, DEFAULT_CONTEXT_PARSER);
    }

    /**
     * Creates new source which will load json from resource input stream.
     * Resource must exist at call time to access its URL.
     * @param language id of language which this source is loading
     * @param classLoader class loader used to get resource
     * @param name resource name
     * @throws FileNotFoundException if
     * @throws URISyntaxException if URL cannot be converted to URI
     */
    @Contract("_, _, _ -> new")
    public static @NotNull GsonTranslationSource fromResource(String language, ClassLoader classLoader, String name)
            throws FileNotFoundException, URISyntaxException {
        var url = classLoader.getResource(name);
        if (url == null) throw new FileNotFoundException("Resource not found");
        return fromStream(url.toURI(), language, () -> {
            var is = classLoader.getResourceAsStream(name);
            if (is == null) throw new IllegalArgumentException("Resource stream not found");
            return is;
        });
    }

    public static @Nullable Translation parseTranslation(Gson gson, JsonElement value) {
        if (value == null) return null;
        if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isString()) {
            var t = value.getAsString();
            return Translation.create(t);
        } else if (value.isJsonObject()) {
            return gson.fromJson(value, Translation.class);
        }
        return null;
    }

    @Override
    public void load(ISourceTextAdder to) {
        var gson = Objects.requireNonNull(gsonSupplier.apply(contextParser),
                "Supplied Gson instance is null");
        var json = jsonSupplier.apply(gson);
        if (json == null) return;
        var obj = json.getAsJsonObject();
        for (var e : obj.entrySet()) {
            var key = e.getKey();
            var v = e.getValue();
            Translation t;
            try {
                t = parseTranslation(gson, v);
            } catch (JsonParseException ex) {
                throw new IllegalArgumentException("Failed to parse translation "+key, ex);
            }
            if (t == null) throw new IllegalArgumentException("Unknown JSON value to use as translation: "+v);
            to.add(key, language, t);
        }
    }

    @Override
    public @Nullable URI getLocation() {
        return location;
    }
}
