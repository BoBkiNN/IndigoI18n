package xyz.bobkinn.indigoi18n.template.arg;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import xyz.bobkinn.indigoi18n.context.Context;
import xyz.bobkinn.indigoi18n.template.format.FormatPattern;

import java.util.function.Function;

/**
 * Interface that is used to create new argument converters by implementing it.
 * <p>
 * Argument converters are used to convert passed object (integer, date, custom one)
 * into target type which is later used by various
 * {@link xyz.bobkinn.indigoi18n.template.format.TemplateFormatter}s to build text.
 * @param <T> argument type which will be converted from
 * @param <O> output type which argument will be converted to
 */
public interface ArgumentConverter<T, O> {

    /**
     * Method which is called to convert argument into output type
     * @param ctx context
     * @param argument passed argument
     * @param format formatting settings which can be used to change conversation behaviour
     * @return conversion result
     */
    O format(@NotNull Context ctx, T argument, FormatPattern format);

    default <NO> ArgumentConverter<T, NO> mapOut(Function<O, NO> mapper) {
        var self = this;
        return (ctx, argument, format) -> mapper.apply(self.format(ctx, argument, format));
    }

    @SuppressWarnings("unused")
    default <NT> ArgumentConverter<NT, O> mapIn(Function<NT, T> mapper) {
        var self = this;
        return (ctx, argument, format) -> self.format(ctx, mapper.apply(argument), format);
    }

    default <NT, NO> ArgumentConverter<NT, NO> map(Function<NT, T> inMapper, Function<O, NO> outMapper) {
        var self = this;
        return (ctx, argument, format) ->
                outMapper.apply(self.format(ctx, inMapper.apply(argument), format));
    }

    @Contract(pure = true)
    static <T> @NotNull ArgumentConverter<T, T> noOp() {
        return (ctx, argument, format) -> argument;
    }

}
