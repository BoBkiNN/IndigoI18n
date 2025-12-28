package xyz.bobkinn.indigoi18n.template;

public record TemplateParseOptions(int initialSeqArgIdx) {

    public TemplateParseOptions() {
        this(0);
    }
}
