package xyz.bobkinn.indigoi18n.context;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.bobkinn.indigoi18n.I18nEngine;
import xyz.bobkinn.indigoi18n.context.impl.I18nContext;
import xyz.bobkinn.indigoi18n.context.impl.LangKeyContext;
import xyz.bobkinn.indigoi18n.context.impl.SourceContext;
import xyz.bobkinn.indigoi18n.data.TranslationInfo;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Context is used to store context entries. Only one instance per entry type can exist, however
 * multiple instances of same entry type can exist across context tree.<br>
 * Methods prefixed with resolve will search for entry in entire context tree.
 */
@RequiredArgsConstructor
public class Context implements ContextEntry {
    private final @Nullable Context parent;
    private final Map<Class<?>, ContextEntry> data = new HashMap<>();

    public Context() {
        this(null);
    }

    public Context sub() {
        return new Context(this);
    }

    /**
     * @return new sub-context with new LangKeyContext set
     */
    public Context sub(String lang, String key, Locale locale) {
        return sub().with(new LangKeyContext(lang, key, locale));
    }

    /**
     * Sets I18n into context if not already.<br>
     * Does nothing if already exists in current context (not tree).
     */
    public void setI18n(I18nEngine i18n) {
        data.computeIfAbsent(I18nContext.class, k -> new I18nContext(i18n));
    }

    public I18nEngine resolveI18n() {
        return resolveOptional(I18nContext.class).map(I18nContext::getI18n).orElse(null);
    }

    /**
     * @return true if {@link I18nContext} found in context tree.
     */
    public boolean isComplete() {
        return resolveI18n() != null;
    }

    /**
     * Combines data maps from this context and other context. Parents are not merged.
     * @param other other context to merge data from
     * @param override if true, existing entries are replaced with new ones
     */
    public void merge(Context other, boolean override) {
        for (var e : other.data.entrySet()) {
            if (override) data.put(e.getKey(), e.getValue());
            else data.computeIfAbsent(e.getKey(), k -> e.getValue());
        }
    }

    public I18nEngine getI18n() {
        return Objects.requireNonNull(resolveI18n(), "No I18n instance in current context tree");
    }

    public <T extends ContextEntry> @Nullable T resolve(Class<T> cls) {
        Context c = this;
        while (c != null) {
            T d = c.get(cls);
            if (d != null) {
                return d;
            }
            c = c.parent;
        }
        return null;
    }

    public <T extends ContextEntry> Optional<T> resolveOptional(Class<T> cls) {
        return Optional.ofNullable(resolve(cls));
    }


    @SuppressWarnings("unchecked")
    public <T extends ContextEntry> @Nullable T get(Class<T> cls) {
        return (T) data.get(cls);
    }

    public <T extends ContextEntry> void set(Class<? extends T> cls, T entry) {
        data.put(cls, entry);
    }

    public <T extends ContextEntry> void set(T entry) {
        set(entry.getClass(), entry);
    }

    public <T extends ContextEntry> Optional<T> getOptional(Class<T> cls) {
        return Optional.ofNullable(get(cls));
    }

    public <T extends ContextEntry, O> Optional<O> getOptional(Class<T> cls, Function<T, O> mapper) {
        return getOptional(cls).map(mapper);
    }

    /**
     * Creates new entry if entry with passed class not found in current context
     * @return found or created entry
     */
    @SuppressWarnings("UnusedReturnValue")
    public <T extends ContextEntry> T compute(Class<T> cls, Supplier<T> creator) {
        var r = get(cls);
        if (r != null) return r;
        var n = creator.get();
        set(cls, n);
        return n;
    }

    public @NotNull String key() {
        var lk = resolveOptional(LangKeyContext.class);
        return lk.map(LangKeyContext::getKey).orElseThrow(() -> new IllegalStateException("No key in context found"));
    }

    public @NotNull String language() {
        var lk = resolveOptional(LangKeyContext.class);
        return lk.map(LangKeyContext::getLang).orElseThrow(() -> new IllegalStateException("No language in context found"));
    }

    /**
     * Resolves {@link LangKeyContext} and {@link SourceContext} and creates {@link TranslationInfo} from them.<br>
     * If {@link SourceContext} not found, resulting info will not contain source.
     * @return null when {@link LangKeyContext} not found
     */
    public TranslationInfo collectInfo() {
        var lk = resolve(LangKeyContext.class);
        var sc = resolve(SourceContext.class);
        if (lk != null && sc != null) {
            return new TranslationInfo(sc.getSource(), lk.getLang(), lk.getKey());
        } else if (lk != null) {
            return new TranslationInfo(null, lk.getLang(), lk.getKey());
        }
        return null;
    }

    public <T extends ContextEntry> Context with(T entry) {
        set(entry.getClass(), entry);
        return this;
    }

    @SuppressWarnings({"unchecked", "UnusedReturnValue"})
    public <T extends ContextEntry> T remove(Class<T> cls) {
        return (T) data.remove(cls);
    }

    /**
     * Resolves locale by stored language. Sets new LangKeyContext into current context
     * @return resolved locale or null if no locale resolved or i18n not provided in this context
     */
    public @Nullable Locale resolveLocale() {
        var lk = resolve(LangKeyContext.class);
        if (lk == null) return null;
        if (lk.getResolvedLocale() != null) return lk.getResolvedLocale();

        var i18n = resolveI18n();
        if (i18n == null) return null;
        var locale = i18n.resolveLocale(lk.getLang());
        if (locale == null) return null;
        var nlk = lk.withLocale(locale);
        set(nlk);
        return locale;
    }

}
