package xyz.bobkinn.indigoi18n.format.adventure;

import lombok.RequiredArgsConstructor;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.translation.Translator;
import net.kyori.adventure.util.TriState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.bobkinn.indigoi18n.IndigoI18n;
import xyz.bobkinn.indigoi18n.format.FormatType;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Adventure {@link Translator} that can be used to access {@link IndigoI18n} pipeline from {@link TranslatableComponent}
 * @see net.kyori.adventure.translation.GlobalTranslator#addSource(Translator)
 * @see Translator
 */
@SuppressWarnings("unused") // TODO add tests for this class
@RequiredArgsConstructor
public class AdventureTranslator implements Translator  {
    /**
     * Name of translator that is used to identify it
     */
    private final Key id;
    /**
     * IndigoI18n instance that will be used
     */
    private final IndigoI18n i18n;
    /**
     * Format type used to perform formatting
     */
    private final FormatType<Component> formatType;

    @Override
    public @NotNull Key name() {
        return id;
    }

    @Override
    public @NotNull TriState hasAnyTranslations() {
        return i18n.getTexts().hasAnyTexts() ? TriState.TRUE : TriState.FALSE;
    }

    @Override
    public boolean canTranslate(@NotNull String key, @NotNull Locale locale) {
        var lang = i18n.getLocaleResolver().getId(locale);
        return i18n.getTexts().has(key, lang);
    }

    @Override
    public @Nullable MessageFormat translate(@NotNull String key, @NotNull Locale locale) {
        return null;
    }

    @Override
    public @Nullable Component translate(@NotNull TranslatableComponent component, @NotNull Locale locale) {
        var format = i18n.getFormat(formatType);
        if (format == null) return null;
        var lang = i18n.getLocaleResolver().getId(locale);
        var key = component.key();
        var ctx = i18n.computeContext(null, key, lang);
        // we don't need to have fallbacks involved here, so using getOrNull.
        //  Missing translation is handled by outer systems.
        var tr = i18n.getResolver().getOrNull(ctx, i18n.getTexts(), key, lang);
        if (tr == null) return null;
        var text = tr.resolve(ctx);
        var args = component.arguments();
        var params = new ArrayList<>(args.size());
        for (var arg : args) {
            params.add(arg.value());
        }
        return format.format(ctx, text, params);
    }

}