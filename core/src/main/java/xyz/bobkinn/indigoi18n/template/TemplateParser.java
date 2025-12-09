package xyz.bobkinn.indigoi18n.template;

import xyz.bobkinn.indigoi18n.data.ParsedEntry;
import xyz.bobkinn.indigoi18n.template.arg.TemplateArgument;
import xyz.bobkinn.indigoi18n.template.format.FormatPattern;

import java.util.ArrayList;
import java.util.function.Consumer;

public class TemplateParser {

    public static TemplateArgument readArg(TemplateReader reader, int seqIdx) {
        reader.consume('{');
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
        reader.consume('}');
        return new TemplateArgument(argIndex, hasExplicitIndex, spec, repr);
    }

    // TODO store source text into TemplateArgument
    public static void parse(String text, Consumer<String> plainConsumer, Consumer<TemplateArgument> argConsumer) {
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
                    plainConsumer.accept(plainBlock.toString());
                    plainBlock.setLength(0);
                }
                // create arg
                argConsumer.accept(new TemplateArgument(seqArgIdx, false, FormatPattern.newDefault(), null));
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
                    plainConsumer.accept(plainBlock.toString());
                    plainBlock.setLength(0);
                }
                // create arg
                argConsumer.accept(new TemplateArgument(aIdx, true, FormatPattern.newDefault(), null));
            } else if (ch == '%' && ch1 == '{') {
                // flush plain
                if (!plainBlock.isEmpty()) {
                    plainConsumer.accept(plainBlock.toString());
                    plainBlock.setLength(0);
                }
                // read arg
                var arg = readArg(reader, seqArgIdx);
                if (!arg.isHasExplicitIndex()) {
                    seqArgIdx++;
                }
                argConsumer.accept(arg);
            } else {
                plainBlock.append(ch);
            }
        }
        if (!plainBlock.isEmpty()) {
            plainConsumer.accept(plainBlock.toString());
        }
    }

    public static ParsedEntry parse(String text) {
        var ls = new ArrayList<>();
        try {
            parse(text, ls::add, ls::add);
        } catch (Exception e) {
            throw new TemplateParseException("Failed to parse text '%s'".formatted(text), e);
        }
        if (ls.isEmpty()) {
            return ParsedEntry.empty();
        }
        return new ParsedEntry(ls);
    }
}
