package xyz.bobkinn.indigoi18n.template;

import org.jetbrains.annotations.NotNull;
import xyz.bobkinn.indigoi18n.data.ParsedEntry;
import xyz.bobkinn.indigoi18n.template.arg.TemplateArgument;

import java.util.ArrayList;

public interface ITemplateParser {
    /**
     * Parses text into different parts which can be consumed by visitor for future processing
     *
     * @param text    template text to parse
     * @param visitor visitor that must be called to later build resulting {@link ParsedEntry}
     * @param options options to parse with
     */
    void parse(String text, TemplateVisitor visitor, TemplateParseOptions options);

    /**
     * Calls {@link #parse(String, TemplateVisitor, TemplateParseOptions)} to collect produced parts into resulting ParsedEntry
     * @param text template text to parse
     * @return new {@link ParsedEntry} containing collected parts
     * @throws TemplateParseException when parsing exception occurs
     */
    default @NotNull ParsedEntry parse(String text, TemplateParseOptions options) throws TemplateParseException {
        var ls = new ArrayList<>();
        var visitor = new TemplateVisitor() {
            @Override
            public void visitPlain(String text) {
                ls.add(text);
            }

            @Override
            public void visitArgument(TemplateArgument argument) {
                ls.add(argument);
            }

            @Override
            public void visitInline(InlineTranslation inline) {
                ls.add(inline);
            }
        };
        try {
            parse(text, visitor, options);
        } catch (Exception e) {
            throw new TemplateParseException("Failed to parse text '%s'".formatted(text), e);
        }
        if (ls.isEmpty()) {
            return ParsedEntry.empty();
        }
        return new ParsedEntry(ls);
    }

    default ParsedEntry parse(String text) throws TemplateParseException {
        return parse(text, new TemplateParseOptions());
    }
}
