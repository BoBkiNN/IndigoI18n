package xyz.bobkinn.indigoi18n.context.impl;

import lombok.Data;
import xyz.bobkinn.indigoi18n.context.ContextEntry;

@Data
public class LangKeyContext implements ContextEntry {
    private final String lang;
    private final String key;
}
