package xyz.bobkinn.indigoi18n.template;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TemplateFormatter {
    private final Map<Class<?>, ArgumentConverter<?, ?>> formatters = new HashMap<>();

    /**
     * @param number number without sign
     */
    public static String alignNumber(FormatSpec.Alignment alignment, int width, String sign, String number) {
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
            case RIGHT -> {
                return alignment.repeatFill(fillCount) + signedNumber;
            }
            case LEFT -> {
                return signedNumber + alignment.repeatFill(fillCount);
            }
            case CENTER -> {
                int rp = (fillCount + 1) / 2;
                int lp = fillCount / 2;
                return alignment.repeatFill(lp) + signedNumber + alignment.repeatFill(rp);
            }
            default -> throw new IllegalArgumentException("Unknown alignment type");
        }
    }

    public static String formatIntGrouped(
            int value,
            int radix,
            int groupSize,
            String separator
    ) {
        // Convert number to requested radix
        String s = Integer.toString(value, radix);

        // No grouping requested
        if (separator == null || groupSize <= 0) {
            return s;
        }

        // Reverse for easier chunking from the end
        StringBuilder sb = new StringBuilder(s).reverse();
        StringBuilder out = new StringBuilder();

        for (int i = 0; i < sb.length(); i++) {
            if (i > 0 && i % groupSize == 0) {
                out.append(separator);
            }
            out.append(sb.charAt(i));
        }

        // Reverse back
        return out.reverse().toString();
    }


    public static final ArgumentConverter<Integer, String> INT_CONVERTER =  (arg, format) -> {
        if (format.getType() == 'c') {
            try {
                return Character.toString(arg);
            } catch (Exception e) {
                // TODO return fallback debug string containing format and raw arg
                throw new RuntimeException(e);
            }
        }
        var sign = arg > 0 ? 1 : (arg < 0 ? -1 : 0);
        var abs = Math.abs(arg);
        var type = format.getType();
        var radix = switch (type) {
            case 'b' -> 2;
            case 'o' -> 8;
            case 'x', 'X' -> 16;
            default -> 10;
        };
        var groupChar = Optional.ofNullable(format.getIntPartGrouping())
                .map(String::valueOf).orElse(null);
        var pmSign = format.getSign().charFor(sign);
        int groupSize = switch (radix) {
            case 2, 8, 16 -> 4;
            default -> 3;
        };
        var uf = formatIntGrouped(abs, radix, groupSize, groupChar);
        if (type == 'X') {
            uf = uf.toUpperCase();
        }
        String outSign = "";
        if (format.isSpecial() && radix == 16) {
            if (type == 'X') outSign = "0X";
            else outSign = "0x";
        } else if (format.isSpecial() && radix == 2) {
            outSign = "0b";
        } else if (format.isSpecial() && radix == 8) {
            outSign = "0o";
        }
        if (pmSign != null) {
            outSign = pmSign + outSign;
        }
        var align = format.getAlignment();
        if (align == null) {
            return outSign+uf;
        }
        return alignNumber(align, format.getWidth(), outSign, uf);
    };
//
//    public Object formatArgument(Object argument, FormatSpec format) {
//
//    }
}
