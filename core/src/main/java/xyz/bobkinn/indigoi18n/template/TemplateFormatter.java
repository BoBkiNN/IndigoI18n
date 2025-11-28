package xyz.bobkinn.indigoi18n.template;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TemplateFormatter {
    private final Map<Class<?>, ArgumentConverter<?, ?>> formatters = new HashMap<>();

    /**
     * @param number number without sign
     */
    public static String alignNumber(FormatSpec.Alignment alignment, Integer width, String sign, String number) {
        if (alignment == null || width == null) {
            return sign+number;
        }
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
            case TO_RIGHT -> {
                return alignment.repeatFill(fillCount) + signedNumber;
            }
            case TO_LEFT -> {
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

    public static String align(FormatSpec.Alignment alignment, Integer width, String text) {
        if (alignment == null || width == null) return text;
        final int fillCount = Math.max(0, width - text.length());
        switch (alignment.type()) {
            case TO_RIGHT, SIGN -> {
                return alignment.repeatFill(fillCount) + text;
            }
            case TO_LEFT -> {
                return text + alignment.repeatFill(fillCount);
            }
            case CENTER -> {
                int rp = (fillCount + 1) / 2;
                int lp = fillCount / 2;
                return alignment.repeatFill(lp) + text + alignment.repeatFill(rp);
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

    public static String pyQuote(String s) {
        boolean hasSingle = s.indexOf('\'') >= 0;
        boolean hasDouble = s.indexOf('"') >= 0;

        char quote;

        if (!hasSingle) {
            // Python prefers single quotes if possible
            quote = '\'';
        } else if (!hasDouble) {
            quote = '"';
        } else {
            // Both exist → choose the less frequent
            long singleCount = s.chars().filter(ch -> ch == '\'').count();
            long doubleCount = s.chars().filter(ch -> ch == '"').count();
            quote = (singleCount <= doubleCount) ? '\'' : '"';
        }

        StringBuilder out = new StringBuilder();
        out.append(quote);

        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);

            // Escape backslashes and the chosen quote
            if (ch == quote || ch == '\\') {
                out.append('\\');
            }
            out.append(ch);
        }

        out.append(quote);
        return out.toString();
    }


    public static final ArgumentConverter<String, String> STRING_CONVERTER =  (arg, format) -> {
        var s = arg;
        if (format.isDoRepr()) {
            s = pyQuote(s);
        }
        // apply precision (cut)
        var pr = format.getPrecision();
        if (pr != null) {
            s = s.substring(0, Math.min(s.length(), pr));
        }
        return align(alignmentOrDefault(format, arg), format.getWidth(), s);
    };


    private static FormatSpec.Alignment getDefaultAlignment(Object arg) {
        if (arg instanceof String) {
            return new FormatSpec.Alignment(FormatSpec.AlignType.TO_LEFT, ' ');
        }
        if (arg instanceof Number) {
            return new FormatSpec.Alignment(FormatSpec.AlignType.TO_RIGHT, ' ');
        }
        return null; // no default
    }

    private static FormatSpec.Alignment alignmentOrDefault(FormatSpec format, Object arg) {
        var s = format.getAlignment();
        if (s != null) return s;
        return getDefaultAlignment(arg);
    }


    public static final ArgumentConverter<Integer, String> INT_CONVERTER =  (arg, format) -> {
        if (format.getType() == 'c') {
            try {
                var s = Character.toString(arg);
                return align(alignmentOrDefault(format, arg), format.getWidth(), s);
            } catch (Exception e) {
                return TemplateArgument.asString(arg, format);
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
        return alignNumber(alignmentOrDefault(format, arg), format.getWidth(), outSign, uf);
    };
//
//    public Object formatArgument(Object argument, FormatSpec format) {
//
//    }
}
