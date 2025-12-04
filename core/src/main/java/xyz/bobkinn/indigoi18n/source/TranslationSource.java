package xyz.bobkinn.indigoi18n.source;

import org.jetbrains.annotations.Nullable;

import java.net.URI;

public interface TranslationSource {

    void load(ITranslationAdder to);

    @Nullable URI getLocation();
}
