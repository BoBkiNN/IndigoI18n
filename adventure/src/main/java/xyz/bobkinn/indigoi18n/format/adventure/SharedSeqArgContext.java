package xyz.bobkinn.indigoi18n.format.adventure;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import xyz.bobkinn.indigoi18n.context.ContextEntry;

@RequiredArgsConstructor
@Data
public final class SharedSeqArgContext implements ContextEntry {
    private final int seqIdx;

    public SharedSeqArgContext() {
        this(0);
    }

    public SharedSeqArgContext inc(int by) {
        return new SharedSeqArgContext(seqIdx+by);
    }
}
