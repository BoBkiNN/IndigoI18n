package xyz.bobkinn.indigoi18n.format;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import xyz.bobkinn.indigoi18n.TemplateCache;
import xyz.bobkinn.indigoi18n.Translations;
import xyz.bobkinn.indigoi18n.data.ParsedEntry;
import xyz.bobkinn.indigoi18n.data.TranslationInfo;
import xyz.bobkinn.indigoi18n.template.format.TemplateFormatter;

import java.util.List;
import java.util.Objects;


@RequiredArgsConstructor
public abstract class I18nFormat<T> {
    protected final TemplateCache cache;
    @Getter
    private TemplateFormatter<T> templateFormatter;

    public void setTemplateFormatter(TemplateFormatter<T> templateFormatter) {
        this.templateFormatter = Objects.requireNonNull(templateFormatter);
    }

    public T replaceArgs(ParsedEntry text, List<Object> args) {
        return templateFormatter.format(text, args);
    }


    /**
     * Produces object from original text
     * @param text original text from {@link Translations}
     */
    public abstract T produce(String text);

    /**
     * Replaces arguments inside input original object
     */
    public abstract T replaceArguments(TranslationInfo info, T input, List<Object> args);

    /**
     * Produces new object and replaces arguments
     * @param text original text
     * @param args arguments
     */
    public T format(TranslationInfo info, String text, List<Object> args) {
        var obj = produce(text);
        return replaceArguments(info, obj, args);
    }

}
