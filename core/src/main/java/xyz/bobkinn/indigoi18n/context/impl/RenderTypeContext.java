package xyz.bobkinn.indigoi18n.context.impl;

import lombok.Data;
import xyz.bobkinn.indigoi18n.context.ContextEntry;
import xyz.bobkinn.indigoi18n.format.RenderType;

@Data
public class RenderTypeContext implements ContextEntry {
    private final RenderType<?> renderType;
}
