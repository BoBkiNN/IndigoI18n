package xyz.bobkinn.indigoi18n.template;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import xyz.bobkinn.indigoi18n.data.ParsedEntry;
import xyz.bobkinn.indigoi18n.template.arg.TemplateArgument;
import xyz.bobkinn.indigoi18n.template.format.FormatPattern;

import java.util.ArrayList;

public class TemplateParser {

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
        return new TemplateArgument(argIndex, hasExplicitIndex, spec, repr);
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

    // TODO store source text into TemplateArgument
    public static void parse(String text, TemplateVisitor visitor) {
        var reader = new TemplateReader(text);
        int seqArgIdx = 0;
        var plainBlock = new StringBuilder();

        while (reader.hasNext()) {
            var ch = reader.next();
            if (!reader.hasNext()) {
                plainBlock.append(ch);
                break;
            }
            var ch1 = reader.peek();
            if (ch == '%' && ch1 == '%') {
                reader.skip();
                plainBlock.append('%');
            } else if (ch == '%' && ch1 == 's') {
                reader.skip();
                // flush plain
                if (!plainBlock.isEmpty()) {
                    visitor.visitPlain(plainBlock.toString());
                    plainBlock.setLength(0);
                }
                // create arg
                visitor.visitArgument(new TemplateArgument(seqArgIdx, false, FormatPattern.newDefault(), null));
                seqArgIdx++;
            } else if (ch == '%' && reader.hasUnsignedNumber()) {
                var aIdx = reader.readUnsignedNumber()-1;
                if (aIdx == -1) {
                    throw new IllegalArgumentException("Argument indexes is starting with 1");
                } else if (aIdx < 0) {
                    throw new IllegalArgumentException("Negative argument index "+aIdx);
                }
                // flush plain
                if (!plainBlock.isEmpty()) {
                    visitor.visitPlain(plainBlock.toString());
                    plainBlock.setLength(0);
                }
                // create arg
                visitor.visitArgument(new TemplateArgument(aIdx, true, FormatPattern.newDefault(), null));
            } else if (ch == '%' && ch1 == '{') {
                // flush plain
                if (!plainBlock.isEmpty()) {
                    visitor.visitPlain(plainBlock.toString());
                    plainBlock.setLength(0);
                }
                reader.consume('{');
                if (reader.tryConsume('t')) {
                    reader.consume(':');
                    // inline translation
                    InlineTranslation tr;
                    try {
                        tr = readInline(reader);
                    } catch (Exception e) {
                        throw new IllegalArgumentException("Failed to read inline translation", e);
                    }
                    visitor.visitInline(tr);
                } else {
                    // read arg
                    var arg = readArg(reader, seqArgIdx);
                    if (!arg.isHasExplicitIndex()) {
                        seqArgIdx++;
                    }
                    visitor.visitArgument(arg);
                }
                reader.consume('}');
            } else {
                plainBlock.append(ch);
            }
        }
        if (!plainBlock.isEmpty()) {
            visitor.visitPlain(plainBlock.toString());
        }
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
