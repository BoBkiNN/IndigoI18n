package xyz.bobkinn.indigoi18n;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.bobkinn.indigoi18n.context.Context;
import xyz.bobkinn.indigoi18n.data.TemplateCache;
import xyz.bobkinn.indigoi18n.data.Translation;
import xyz.bobkinn.indigoi18n.data.TranslationInfo;
import xyz.bobkinn.indigoi18n.format.RenderType;
import xyz.bobkinn.indigoi18n.format.Renderer;
import xyz.bobkinn.indigoi18n.resolver.DefaultTranslationResolver;
import xyz.bobkinn.indigoi18n.resolver.TranslationResolver;
import xyz.bobkinn.indigoi18n.source.TranslationSource;
import xyz.bobkinn.indigoi18n.template.ITemplateParser;
import xyz.bobkinn.indigoi18n.template.TemplateParser;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;


/**
 * I18n with no renderers or its access methods.<br>
 * Uses default resolver and do not include any translations
 * @see StringI18n
 */
public class IndigoI18n implements I18nEngine {
    @Getter
    protected final Translations texts;
    /**
     * Resolver that is used to lookup texts to find text by key and language
     */
    @Getter
    private @NotNull TranslationResolver resolver;
    @Getter
    private final Map<RenderType<?>, Renderer<?>> renderers;

    @Getter
    private final LocaleResolver localeResolver;

    private final Map<String, Locale> localeCache = new ConcurrentHashMap<>();

    public IndigoI18n(ITemplateParser templateParser, LocaleResolver localeResolver) {
        texts = new Translations(new TemplateCache(templateParser));
        resolver = new DefaultTranslationResolver();
        renderers = new HashMap<>();
        this.localeResolver = localeResolver;
    }

    public IndigoI18n() {
        this(TemplateParser.INSTANCE, LocaleResolver.DEFAULT);
    }

    /**
     * Adds default formats
     */
    public void setup() {
        addDefaultRenderers();
    }

    public <T> void addRenderer(RenderType<T> t, Function<TemplateCache, Renderer<T>> factory) {
        renderers.put(t, factory.apply(texts.getCache()));
    }

    protected void addDefaultRenderers() {
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Renderer<T> getRenderer(RenderType<T> ft) {
        var f = (Renderer<T>) renderers.get(ft);
        return Objects.requireNonNull(f, "No format for type "+ft);
    }

    /**
     * Remove renderer by its type from renderers map
     * @param rt render type to match
     * @return true if removed
     */
    @SuppressWarnings("unused")
    public boolean removeRenderer(RenderType<?> rt) {
        return renderers.remove(rt) != null;
    }

    public void load(TranslationSource source) {
        texts.load(source);
    }

    public void unload(TranslationSource source) {
        texts.unload(source);
    }

    @SuppressWarnings("unused")
    public List<TranslationSource> sources() {
        return texts.sources();
    }

    @Override
    public TranslationInfo infoFor(String lang, String key) {
        return texts.infoFor(key, lang);
    }

    @SuppressWarnings("unused")
    public void setResolver(TranslationResolver resolver) {
        this.resolver = Objects.requireNonNull(resolver);
    }

    @Override
    public @Nullable Locale resolveLocale(String langId) {
        if (localeResolver == null) return null;
        return localeCache.computeIfAbsent(langId, localeResolver::getLocale);
    }

    @Override
    public void resetLocaleCache() {
        localeCache.clear();
    }

    @Override
    public Translation get(Context context, String key, String language) {
        return resolver.get(context, texts, key, language);
    }

}
