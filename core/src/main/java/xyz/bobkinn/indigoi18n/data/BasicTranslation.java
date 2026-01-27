package xyz.bobkinn.indigoi18n.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import xyz.bobkinn.indigoi18n.context.Context;

@RequiredArgsConstructor
@Getter
public class BasicTranslation extends Translation {
    private final String text;

    public BasicTranslation(String text, Context contextOverride) {
        super(contextOverride);
        this.text = text;
    }

    @Override
    public @NotNull String resolve(Context ctx) {
        return text;
    }

    @Override
    public void createCache(TemplateCache cache, TranslationInfo info) {
        cache.computeCache(text, info);
    }

    @Override
    public void resetCache(TemplateCache cache) {
        cache.resetCache(text);
    }
}
