package xyz.bobkinn.indigoi18n.context;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.bobkinn.indigoi18n.I18nBase;
import xyz.bobkinn.indigoi18n.context.impl.LangKeyContext;
import xyz.bobkinn.indigoi18n.context.impl.SourceContext;
import xyz.bobkinn.indigoi18n.data.TranslationInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class Context implements ContextEntry {
    private final @Nullable Context parent;
    @Getter
    private final @Nullable I18nBase i18n;
    private final Map<Class<?>, ContextEntry> data = new HashMap<>();

    public Context() {
        this(null, null);
    }

    public Context sub() {
        return new Context(this, null);
    }

    public boolean isComplete() {
        return i18n != null;
    }

    public void merge(Context other) {
        for (var e : other.data.entrySet()) {
            data.computeIfAbsent(e.getKey(), k -> e.getValue());
        }
    }

    public I18nBase resolveI18n() {
        var v = i18n;
        var p = parent;
        while (v == null && p != null) {
            v = p.i18n;
            p = p.parent;
        }
        return Objects.requireNonNull(v, "No I18n instance in current context tree");
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

}
