package xyz.bobkinn.indigoi18n.data;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import xyz.bobkinn.indigoi18n.template.InlineTranslation;
import xyz.bobkinn.indigoi18n.template.TemplateVisitor;
import xyz.bobkinn.indigoi18n.template.arg.TemplateArgument;

import java.util.List;

/**
 * This class represents parsed text.
 * @param parts Each part is either String or {@link TemplateArgument} or {@link InlineTranslation}.
 *              Order of elements must be saved. Must be not empty. <br>
 *              Actually you can store any custom parts here by avoiding {@link #visit(TemplateVisitor)}
 */
public record ParsedEntry(List<Object> parts) {

    public Object part(int index) {
        return parts.get(index);
    }

    /**
     * @param index global part index
     * @throws ClassCastException when part is not TemplateArgument
     */
    public TemplateArgument argumentAt(int index) {
        var part = parts.get(index);
        return (TemplateArgument) part;
    }

    public void visit(TemplateVisitor visitor) {
        for (var part : parts) {
            if (part instanceof String s) visitor.visitPlain(s);
            else if (part instanceof TemplateArgument arg) visitor.visitArgument(arg);
            else if (part instanceof InlineTranslation inline) visitor.visitInline(inline);
            else throw new IllegalArgumentException("Unknown part type: "+part);
        }
    }

    /**
     * Used to create empty text - empty means it only contains empty string since entry must contain parts
     * @return new entry with empty string in list
     */
    @Contract(" -> new")
    public static @NotNull ParsedEntry empty() {
        return new ParsedEntry(List.of(""));
    }
}
