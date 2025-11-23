package xyz.bobkinn.indigoi18n.template;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class TemplateArgument {
    private final int index;
    private final boolean hasExplicitIndex;
    private final FormatSpec formatSpec;
}
