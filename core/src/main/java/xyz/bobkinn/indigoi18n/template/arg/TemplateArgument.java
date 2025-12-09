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
    private final Character repr;

    public boolean isRepr(char mode) {
        return repr != null && repr == mode;
    }

    public boolean isRepr(char... modes) {
        if (repr == null) return false;
        for (var mode : modes) {
            if (repr == mode) return true;
        }
        return false;
    }
}
