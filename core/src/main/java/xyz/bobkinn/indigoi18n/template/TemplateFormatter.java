package xyz.bobkinn.indigoi18n.template;

import java.util.HashMap;
import java.util.Map;

public class TemplateFormatter {
    private final Map<Class<?>, ArgumentConverter<?, ?>> formatters = new HashMap<>();

    /**
     * @param number number without sign
     */
    public static String alignNumber(FormatSpec.Alignment alignment, int width, Character sign, String number) {
        final String signedNumber = sign != null ? sign + number : number;
        final int fillCount = Math.max(0, width - signedNumber.length());
        switch (alignment.type()) {
            case SIGN -> {
                var sb = new StringBuilder();
                if (sign != null) {
                    sb.append(sign);
                }
                sb.append(String.valueOf(alignment.fill()).repeat(fillCount));
                sb.append(number);
                return sb.toString();
            }
            case LEFT -> {
                return alignment.repeatFill(fillCount) + signedNumber;
            }
            case RIGHT -> {
                return signedNumber + alignment.repeatFill(fillCount);
            }
            case CENTER -> {
                int rp = (width + 1) / 2;
                int lp = width / 2;
                return alignment.repeatFill(lp) + signedNumber + alignment.repeatFill(rp);
            }
            default -> throw new IllegalArgumentException("Unknown alignment type");
        }
    }

//    public static final ArgumentConverter<Integer, String> INT_CONVERTER =  (arg, format) -> {
//
//    };
//
//    public Object formatArgument(Object argument, FormatSpec format) {
//
//    }
}
