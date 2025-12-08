package xyz.bobkinn.indigoi18n.template.arg;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import xyz.bobkinn.indigoi18n.template.format.FormatSpec;

@RequiredArgsConstructor
@Getter
public class TemplateArgument {
    private final int index;
    private final boolean hasExplicitIndex;
    private final FormatSpec formatSpec;

    public static String asString(Object arg, FormatSpec format) {
        return "%%{%s:%s}".formatted(arg, format.getSource());
    }
}
