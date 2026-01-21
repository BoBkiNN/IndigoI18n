package xyz.bobkinn.indigoi18n.format;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Render type describes type of {@link Renderer}
 * It is used to have different {@link Renderer} with different flavors.
 * @param <T> renderer output type
 */
@Data
@RequiredArgsConstructor
public final class RenderType<T> {
    /**
     * Default type describing String renderer with no flavor
     */
    public static final RenderType<String> STRING = new RenderType<>(String.class);

    private final Class<T> outputType;
    private final @Nullable String flavor;

    public RenderType(Class<T> outputType) {
        this(outputType, null);
    }


    @Contract(pure = true)
    @Override
    public @NotNull String toString() {
        var n = outputType.getSimpleName();
        return flavor != null ? n+"("+flavor+")" : n;
    }
}
