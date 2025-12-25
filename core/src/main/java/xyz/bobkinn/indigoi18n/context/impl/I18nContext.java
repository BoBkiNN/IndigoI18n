package xyz.bobkinn.indigoi18n.context.impl;

import lombok.Data;
import xyz.bobkinn.indigoi18n.I18nEngine;
import xyz.bobkinn.indigoi18n.context.ContextEntry;

@Data
public class I18nContext implements ContextEntry {
    private final I18nEngine i18n;
}
