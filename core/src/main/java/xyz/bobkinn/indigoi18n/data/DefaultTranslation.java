package xyz.bobkinn.indigoi18n.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import xyz.bobkinn.indigoi18n.context.Context;

@RequiredArgsConstructor
@Getter
public class DefaultTranslation extends Translation {
    private final String text;

    @Override
    public String get(Context ctx) {
        return text;
    }

    @Override
    public void createCache(TemplateCache cache, TranslationInfo info) {
        cache.createCache(text, info);
    }

    @Override
    public void resetCache(TemplateCache cache) {
        cache.resetCache(text);
    }
}
