package xyz.bobkinn.indigoi18n.source.impl;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.bobkinn.indigoi18n.source.ISourceTextAdder;
import xyz.bobkinn.indigoi18n.source.TranslationLoadError;
import xyz.bobkinn.indigoi18n.source.TranslationSource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class GsonTranslationSource implements TranslationSource {

    private static final TypeToken<Map<String, String>> TOKEN = new TypeToken<>() {
    };

    private final @Nullable URI location;
    private final Function<Gson, JsonElement> jsonSupplier;
    private final Supplier<Gson> gsonSupplier;
    private final String language;

    private static final Supplier<Gson> DEFAULT_GSON_SUPPLIER = Gson::new;

    @Contract("_, _, _ -> new")
    public static @NotNull GsonTranslationSource fromElement(@Nullable URI location, String language, JsonElement element) {
        return new GsonTranslationSource(location, (g) -> element, DEFAULT_GSON_SUPPLIER, language);
    }

    @Contract("_, _, _ -> new")
    public static @NotNull GsonTranslationSource fromFile(@Nullable URI location, String language, File file) {
        return new GsonTranslationSource(location, (g) -> {
            try (var fr = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
                return g.fromJson(fr, JsonObject.class);
            } catch (IOException e) {
                throw new TranslationLoadError("IO exception when reading json object", e);
            }
        }, DEFAULT_GSON_SUPPLIER, language);
    }

    @Override
    public void load(ISourceTextAdder to) {
        var gson = Objects.requireNonNull(gsonSupplier.get(),
                "Supplied Gson instance is null");
        var json = jsonSupplier.apply(gson);
        if (json == null) return;
        var map = gson.fromJson(json, TOKEN);
        for (var e : map.entrySet()) {
            to.add(e.getKey(), language, e.getValue());
        }
    }

    @Override
    public @Nullable URI getLocation() {
        return location;
    }
}
