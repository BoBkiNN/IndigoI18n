package xyz.bobkinn.indigoi18n.context.impl;

import lombok.Data;
import xyz.bobkinn.indigoi18n.context.ContextEntry;
import xyz.bobkinn.indigoi18n.format.FormatType;

@Data
public class FormatTypeContext implements ContextEntry {
    private final FormatType<?> formatType;
}
