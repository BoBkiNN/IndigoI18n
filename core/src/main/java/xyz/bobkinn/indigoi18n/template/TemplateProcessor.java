package xyz.bobkinn.indigoi18n.template;

import java.util.function.Consumer;

public class TemplateProcessor {

    public TemplateArgument readArg(TemplateReader reader, int seqIdx) {
        reader.consume('{');
        int argIndex = seqIdx;
        boolean hasExplicitIndex = false;
        if (reader.hasUnsignedNumber()) {
            argIndex = reader.readUnsignedNumber();
            hasExplicitIndex = true;
        } else if (reader.peek() == 's') {
            reader.skip();
        }
        final boolean doRepr;
        if (reader.tryConsume('!')) {
            // conversion
            var ct = reader.next();
            if (ct == 'r') doRepr = true;
            else if (ct == 's') doRepr = false;
            else throw new IllegalArgumentException("Unknown conversion "+ct);
        } else {
            doRepr = false;
        }
        final FormatSpec spec;
        if (reader.tryConsume(':')) {
            spec = FormatSpec.readFormatSpec(reader, doRepr);
        } else {
            spec = null;
        }
        reader.consume('}');
        return new TemplateArgument(argIndex, hasExplicitIndex, spec);
    }

    public void parse(String text, Consumer<String> plainConsumer, Consumer<TemplateArgument> argConsumer) {
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
                plainConsumer.accept(plainBlock.toString());
                plainBlock.setLength(0);
                // create arg
                argConsumer.accept(new TemplateArgument(seqArgIdx, false, null));
                seqArgIdx++;
            } else if (ch == '%' && reader.hasUnsignedNumber()) {
                var aIdx = reader.readUnsignedNumber();
                // flush plain
                plainConsumer.accept(plainBlock.toString());
                plainBlock.setLength(0);
                // create arg
                argConsumer.accept(new TemplateArgument(aIdx, true, null));
            } else if (ch == '%' && ch1 == '{') {
                // flush plain
                plainConsumer.accept(plainBlock.toString());
                plainBlock.setLength(0);
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
}
