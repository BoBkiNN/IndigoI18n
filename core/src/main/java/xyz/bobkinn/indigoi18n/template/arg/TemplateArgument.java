package xyz.bobkinn.indigoi18n.template.arg;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import xyz.bobkinn.indigoi18n.template.format.FormatPattern;

@RequiredArgsConstructor
@Getter
public class TemplateArgument {
    private final int index;
    private final boolean hasExplicitIndex;
    private final FormatPattern pattern;
    private final boolean doRepr;

    public static String asString(Object arg, FormatPattern pattern) {
        return "%%{%s:%s}".formatted(arg, pattern.getSource());
    }
}
