package xyz.bobkinn.indigoi18n.context.impl;

import lombok.Data;
import xyz.bobkinn.indigoi18n.context.ContextEntry;

@Data
public class CountContext implements ContextEntry {
    private final int count;
}
