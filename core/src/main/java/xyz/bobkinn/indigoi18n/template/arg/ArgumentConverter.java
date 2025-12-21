package xyz.bobkinn.indigoi18n.template.arg;

import org.jetbrains.annotations.NotNull;
import xyz.bobkinn.indigoi18n.context.Context;
import xyz.bobkinn.indigoi18n.template.format.FormatPattern;

public interface ArgumentConverter<T, O> {
    O format(@NotNull Context ctx, T argument, FormatPattern format);
}
