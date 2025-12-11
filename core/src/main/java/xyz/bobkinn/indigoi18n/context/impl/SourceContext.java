package xyz.bobkinn.indigoi18n.context.impl;

import lombok.Data;
import xyz.bobkinn.indigoi18n.context.ContextEntry;
import xyz.bobkinn.indigoi18n.source.TranslationSource;

@Data
public class SourceContext implements ContextEntry {
    private final TranslationSource source;
}
