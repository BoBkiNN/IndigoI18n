package xyz.bobkinn.indigoi18n.data;

import xyz.bobkinn.indigoi18n.template.TemplateArgument;

import java.util.List;
import java.util.function.Consumer;

public record ParsedEntry(List<Object> parts) {
    public void process(Consumer<String> textConsumer, Consumer<TemplateArgument> argConsumer) {
        for (var part : parts) {
            if (part instanceof String s) textConsumer.accept(s);
            else if (part instanceof TemplateArgument arg) argConsumer.accept(arg);
            else throw new IllegalArgumentException("Unknown part type: "+part);
        }
    }
}
