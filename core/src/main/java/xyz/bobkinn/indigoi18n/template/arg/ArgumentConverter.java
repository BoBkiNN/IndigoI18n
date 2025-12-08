package xyz.bobkinn.indigoi18n.template.arg;

import xyz.bobkinn.indigoi18n.template.format.FormatSpec;

public interface ArgumentConverter<T, O> {
    O format(T argument, FormatSpec format);
}
