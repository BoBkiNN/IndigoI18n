package xyz.bobkinn.indigoi18n.template;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import xyz.bobkinn.indigoi18n.data.ParsedEntry;
import xyz.bobkinn.indigoi18n.template.arg.TemplateArgument;
import xyz.bobkinn.indigoi18n.template.format.FormatPattern;

import java.util.ArrayList;

public class TemplateParser {

    /**
     * @return new TemplateArgument with source containing '%{' and '}' around
     */
    public static TemplateArgument readArg(TemplateReader reader, int seqIdx) {
        int argIndex = seqIdx;
        boolean hasExplicitIndex = false;
        if (reader.hasUnsignedNumber()) {
            argIndex = reader.readUnsignedNumber()-1;
            if (argIndex == -1) {
                throw new IllegalArgumentException("Argument indexes is starting with 1");
            } else if (argIndex < 0) {
                throw new IllegalArgumentException("Negative argument index "+argIndex);
            }
            hasExplicitIndex = true;
        } else if (reader.peek() == 's') {
            reader.skip();
        }
        final Character repr;
        if (reader.tryConsume('!')) {
            // conversion
            repr = reader.next();
        } else {
            repr = null;
        }
        final FormatPattern spec;
        if (reader.tryConsume(':')) {
            spec = FormatPattern.readFormatSpec(reader);
        } else {
            spec = FormatPattern.newDefault();
        }
        var src = "%{" + reader.markedPart() + "}";
        return new TemplateArgument(src, argIndex, hasExplicitIndex, spec, repr);
    }

    @Contract("_ -> new")
    public static @NotNull InlineTranslation readInline(@NotNull TemplateReader reader) {
        StringBuilder key = new StringBuilder();
        char peeked = reader.peek();
        while (reader.hasNext() && peeked != ':' && peeked != '}') {
            key.append(reader.next());
            if (!reader.hasNext()) break;
            peeked = reader.peek();
        }
        if (key.isEmpty() || key.toString().isBlank()) {
            throw new IllegalArgumentException("key is empty");
        }
        int depth = 1;
        String lang = null;
        if (reader.tryConsume(':')) {
            if (reader.hasUnsignedNumber()) {
                depth = reader.readUnsignedNumber();
            }
            // second colon
            if (reader.tryConsume(':')) {
                lang = reader.readUntil("}", false); // do not require delimiter for tests
                if (lang.isEmpty()) {
                    // treat missing language as default language
                    lang = null;
                } else if (lang.isBlank()) {
                    throw new IllegalArgumentException("language is blank");
                }
            }
        }
        return new InlineTranslation(key.toString(), depth, lang);
    }

    private static int parseAdvanced(
            TemplateReader reader,
            TemplateVisitor visitor,
            int seqArgIdx
    ) {
        reader.pushMark(); // pos is after %{
        if (reader.tryConsume('t')) {
            reader.consume(':');
            try {
                visitor.visitInline(readInline(reader));
            } catch (Exception e) {
                throw new IllegalArgumentException("Failed to read inline translation", e);
            } finally {
                reader.popMark(false); // pop mark before return
            }
            return seqArgIdx;
        }

        var arg = readArg(reader, seqArgIdx);
        if (!arg.isHasExplicitIndex()) {
            seqArgIdx++;
        }
        visitor.visitArgument(arg);
        reader.popMark(false); // pop push mark earlier
        return seqArgIdx;
    }

    public static void parse(String text, TemplateVisitor visitor) {
        var reader = new TemplateReader(text);
        int seqArgIdx = 0;
        var plain = new StringBuilder();

        Runnable flush = () -> {
            if (!plain.isEmpty()) {
                visitor.visitPlain(plain.toString());
                plain.setLength(0);
            }
        };

        while (reader.hasNext()) {
            char ch = reader.next();

            if (ch != '%') { // not a start of template entry
                plain.append(ch);
                continue;
            }

            if (!reader.hasNext()) { // check for %<EOL> here so peek() wont throw error
                plain.append('%');
                break;
            }

            char next = reader.peek();

            switch (next) {
                case '%' -> { // %%
                    reader.skip();
                    plain.append('%');
                }

                case 's' -> { // %s
                    reader.skip();
                    flush.run();
                    visitor.visitArgument(
                            new TemplateArgument("%s", seqArgIdx++, false, FormatPattern.newDefault(), null)
                    );
                }

                case '{' -> { // %{...}
                    flush.run();
                    reader.consume('{');
                    seqArgIdx = parseAdvanced(reader, visitor, seqArgIdx);
                    reader.consume('}');
                }

                default -> { // %1 OR %just text
                    if (reader.hasUnsignedNumber()) {
                        flush.run();
                        int idx = reader.readUnsignedNumber() - 1;
                        if (idx < 0) throw new IllegalArgumentException("Argument indexes start from 1");

                        visitor.visitArgument(
                                new TemplateArgument("%"+(idx+1), idx, true,
                                        FormatPattern.newDefault(), null)
                        );
                    } else plain.append('%');
                }
            }
        }

        flush.run();
    }


    public static ParsedEntry parse(String text) {
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
            parse(text, visitor);
        } catch (Exception e) {
            throw new TemplateParseException("Failed to parse text '%s'".formatted(text), e);
        }
        if (ls.isEmpty()) {
            return ParsedEntry.empty();
        }
        return new ParsedEntry(ls);
    }
}
