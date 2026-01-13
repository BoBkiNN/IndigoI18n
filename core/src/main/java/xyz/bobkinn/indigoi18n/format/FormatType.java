package xyz.bobkinn.indigoi18n.format;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Format type describes type of {@link I18nFormat}
 * It is used to have different {@link I18nFormat} with different flavors.
 * @param <T> formatting output type
 */
@Data
@RequiredArgsConstructor
public final class FormatType<T> {
    /**
     * Default type describing String format with no flavor
     */
    public static final FormatType<String> STRING_FORMAT_TYPE = new FormatType<>(String.class);

    private final Class<T> outputType;
    private final @Nullable String flavor;

    public FormatType(Class<T> outputType) {
        this(outputType, null);
    }


    @Contract(pure = true)
    @Override
    public @NotNull String toString() {
        var n = outputType.getSimpleName();
        return flavor != null ? n+"("+flavor+")" : n;
    }
}
