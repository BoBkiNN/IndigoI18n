package xyz.bobkinn.indigoi18n.source;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

@SuppressWarnings("unused")
@RequiredArgsConstructor
public abstract class StreamTranslationsSource implements TranslationSource {
    private final URI location;

    public abstract InputStream open();

    @Override
    public void load(ISourceTextAdder to) {
        try (var is = open()) {
            try {
                read(is, to);
            } catch (IOException e) {
                throw new TranslationLoadError("Failed to read translations stream", e);
            }
        } catch (IOException e) {
            throw new TranslationLoadError("Failed to open/close translations stream", e);
        } catch (TranslationLoadError e) {
            throw e;
        } catch (Exception e) {
            throw new TranslationLoadError("Failed to load stream translations source", e);
        }
    }

    protected abstract void read(InputStream stream, ISourceTextAdder adder) throws IOException;

    @Override
    public @Nullable URI getLocation() {
        return location;
    }
}
