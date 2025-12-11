package xyz.bobkinn.indigoi18n.template;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

// @Data includes @RequiredArgsConstructor but intellij lombok plugin
// does not add ctor for use in overloads
@Data
@RequiredArgsConstructor
public class InlineTranslation {
    private final String key;
    private final int depth;
    private final @Nullable String lang;

    public InlineTranslation(String key) {
        this(key, 1, null);
    }
}
