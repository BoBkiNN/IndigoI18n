package xyz.bobkinn.indigoi18n.context.impl;

import lombok.Data;
import xyz.bobkinn.indigoi18n.I18nBase;
import xyz.bobkinn.indigoi18n.context.ContextEntry;

@Data
public class I18nContext implements ContextEntry {
    private final I18nBase i18n;
}
