package xyz.bobkinn.indigoi18n.template.arg;

import xyz.bobkinn.indigoi18n.template.format.FormatPattern;

public interface ArgumentConverter<T, O> {
    O format(T argument, FormatPattern format);
}
