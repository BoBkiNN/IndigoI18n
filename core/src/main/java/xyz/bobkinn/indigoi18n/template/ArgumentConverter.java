package xyz.bobkinn.indigoi18n.template;

public interface ArgumentConverter<T, O> {
    O format(T argument, FormatSpec format);
}
