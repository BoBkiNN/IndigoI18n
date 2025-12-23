package xyz.bobkinn.indigoi18n.template.arg;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.bobkinn.indigoi18n.context.Context;
import xyz.bobkinn.indigoi18n.context.impl.LangKeyContext;
import xyz.bobkinn.indigoi18n.template.Utils;
import xyz.bobkinn.indigoi18n.template.format.FormatPattern;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;

public class ArgConverters {

    /**
     * @param number number without sign
     */
    public static String alignNumber(FormatPattern.Alignment alignment, Integer width, String sign, String number) {
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
                sb.append(alignment.repeatFill(fillCount));
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

    public static String align(FormatPattern.Alignment alignment, Integer width, String text) {
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


    public static final ArgumentConverter<String, String> STRING_CONVERTER = (ctx, arg, format) -> applyGenericStringFormat(arg, format);

    private static String applyGenericStringFormat(String arg, FormatPattern format) {
        var s = arg;
        // apply precision (cut)
        var pr = format.getPrecision();
        if (pr != null) {
            s = s.substring(0, Math.min(s.length(), pr));
        }
        return align(alignmentOrDefault(format, arg), format.getWidth(), s);
    }


    private static FormatPattern.Alignment getDefaultAlignment(Object arg) {
        if (arg instanceof String) {
            return new FormatPattern.Alignment(FormatPattern.AlignType.TO_LEFT, ' ');
        }
        if (arg instanceof Number) {
            return new FormatPattern.Alignment(FormatPattern.AlignType.TO_RIGHT, ' ');
        }
        return null; // no default
    }

    private static FormatPattern.Alignment alignmentOrDefault(FormatPattern format, Object arg) {
        var s = format.getAlignment();
        if (s != null) return s;
        return getDefaultAlignment(arg);
    }


    public static final ArgumentConverter<Number, String> INT_CONVERTER = (ctx, n, format) -> {
        var arg = n.longValue();
        char type = format.getType();
        // char
        if (type == 'c') {
            try {
                var s = Character.toString((int) arg);
                return align(alignmentOrDefault(format, arg), format.getWidth(), s);
            } catch (Exception e) {
                return FormatPattern.asString(arg, format);
            }
        }

        /* =========================
         * locale-aware integer ('n')
         * ========================= */
        if (type == 'n') {
            Locale locale = contextLanguage(ctx);
            if (locale == null) locale = Locale.ROOT;

            NumberFormat nf = NumberFormat.getIntegerInstance(locale);
            nf.setGroupingUsed(true);

            String formatted = nf.format(Math.abs(arg));

            int sign = Long.compare(arg, 0);
            Character signChar = format.getSign().charFor(sign);
            String signStr = signChar == null ? "" : String.valueOf(signChar);

            return alignNumber(
                    alignmentOrDefault(format, arg),
                    format.getWidth(),
                    signStr,
                    formatted
            );
        }

        /* =========================
         * existing logic
         * ========================= */

        int sign = Long.compare(arg, 0);
        var abs = Math.abs(arg);

        int radix = switch (type) {
            case 'b' -> 2;
            case 'o' -> 8;
            case 'x', 'X' -> 16;
            default -> 10;
        };

        var groupChar = Optional.ofNullable(format.getIntPartGrouping())
                .map(String::valueOf).orElse(null);

        int groupSize = switch (radix) {
            case 2, 8, 16 -> 4;
            default -> 3;
        };
        var uf = Utils.formatLongGrouped(abs, radix, groupSize, groupChar);
        if (type == 'X') {
            uf = uf.toUpperCase();
        }
        String outSign = "";
        if (format.isSpecial()) {
            if (radix == 16) outSign = (type == 'X') ? "0X" : "0x";
            else if (radix == 2) outSign = "0b";
            else if (radix == 8) outSign = "0o";
        }

        var pmSign = format.getSign().charFor(sign);
        if (pmSign != null) {
            outSign = pmSign + outSign;
        }
        return alignNumber(alignmentOrDefault(format, arg), format.getWidth(), outSign, uf);
    };


    /**
     * double and float
     */
    @SuppressWarnings("MalformedFormatString")
    public static final ArgumentConverter<Number, String> NUMBER_CONVERTER = (ctx, arg, format) -> {
        double value = arg.doubleValue();
        char type = format.getType();
        Integer precision = format.getPrecision();
        if (precision == null) precision = 6;

        boolean isNaN = Double.isNaN(value);
        boolean isInf = Double.isInfinite(value);

        int sigNum = isNaN ? 1 : (value > 0 ? 1 : (value < 0 ? -1 : 0));
        Character signChar = format.getSign().charFor(sigNum);

        if (isNaN) {
            String out = "nan";
            if (Character.isUpperCase(type)) out = out.toUpperCase();
            return align(alignmentOrDefault(format, arg), format.getWidth(),
                    signChar == null ? out : signChar + out);
        }

        if (isInf) {
            String out = "inf";
            if (Character.isUpperCase(type)) out = out.toUpperCase();
            return align(alignmentOrDefault(format, arg), format.getWidth(),
                    signChar == null ? out : signChar + out);
        }

        double abs = Math.abs(value);
        String formatted;

        if (type == 'n') {
            Locale locale = contextLanguage(ctx);
            if (locale == null) locale = Locale.ROOT;

            var nf = NumberFormat.getNumberInstance(locale);

            nf.setGroupingUsed(true);
            if (nf instanceof DecimalFormat df) df.setMinimumFractionDigits(precision);

            formatted = nf.format(abs);
        } else {
            // old behavior
            switch (type) {
                case 'e', 'E' -> {
                    formatted = String.format(Locale.ROOT, "%." + precision + "e", abs);
                    if (type == 'E') formatted = formatted.toUpperCase();
                }
                case 'f', 'F' -> {
                    formatted = String.format(Locale.ROOT, "%." + precision + "f", abs);
                    if (type == 'F') formatted = formatted.toUpperCase();
                }
                case '%' -> {
                    double pct = abs * 100.0;
                    formatted = String.format(Locale.ROOT, "%." + precision + "f%%", pct);
                }
                default -> formatted = Double.toString(abs);
            }

            // Apply integer-part grouping if requested
            boolean isE = formatted.indexOf('e') > 0 || formatted.indexOf('E') > 0;
            if (format.getIntPartGrouping() != null && !isE) {
                String[] parts = formatted.split("\\."); // split integer and fraction
                String intPart = parts[0];
                String fracPart = parts.length > 1 ? parts[1] : null;
                String separator = format.getIntPartGrouping().toString();
                intPart = Utils.formatIntGrouped(intPart, 3, separator); // base is 10 so group by 3
                formatted = fracPart != null ? intPart + "." + fracPart : intPart;
            }

            if (format.isSpecial()) {
                var dotIdx = formatted.indexOf('.');
                var fracZero = Utils.fractionalPartIsZero(formatted, dotIdx);
                if (dotIdx > 0 && fracZero) {
                    var esIdx = formatted.indexOf('e');
                    var ebIdx = formatted.indexOf('E');
                    var eIdx = Math.max(esIdx, ebIdx);
                    if (eIdx > 0) {
                        var ePart = formatted.substring(eIdx);
                        var wholePart = formatted.substring(0, dotIdx);
                        formatted = wholePart + ePart;
                    } else formatted = formatted.substring(0, dotIdx);

                    if (type == '%') formatted += "%";
                }
            }
        }

        String signStr = signChar == null ? "" : String.valueOf(signChar);
        return alignNumber(alignmentOrDefault(format, arg), format.getWidth(), signStr, formatted);
    };


    private static @Nullable Locale contextLanguage(@NotNull Context ctx) {
        var lang = ctx.resolveOptional(LangKeyContext.class)
                .map(LangKeyContext::getLang).orElse(null);
        var i18n = ctx.resolveI18n();
        if (i18n != null) {
            var lr = i18n.getLocaleResolver();
            return lr.getLocale(lang);
        } else return null;
    }

    public static final ArgumentConverter<TemporalAccessor, String> TEMPORAL_CONVERTER =
            (ctx, arg, format) -> {
        var s = DateFormatUtil.format(arg, format.getType(), contextLanguage(ctx));
        return applyGenericStringFormat(s, format);
    };

    public static final ArgumentConverter<Date, String> DATE_CONVERTER =
            (ctx, arg, format) -> {
                var s = DateFormatUtil.format(arg, format.getType(), contextLanguage(ctx));
                return applyGenericStringFormat(s, format);
            };

    public static final ArgumentConverter<Calendar, String> CALENDAR_CONVERTER =
            (ctx, arg, format) -> {
                var s = DateFormatUtil.format(arg, format.getType(), contextLanguage(ctx));
                return applyGenericStringFormat(s, format);
            };

    public static final ArgumentConverter<BigInteger, String> BIG_INT_CONVERTER =
            (ctx, arg, format) -> {

                int sign = arg.signum();
                BigInteger abs = arg.abs();
                char type = format.getType();

                // Handle locale-aware 'n' mode
                if (type == 'n') {
                    Locale locale = contextLanguage(ctx);
                    if (locale == null) locale = Locale.ROOT;

                    NumberFormat nf = NumberFormat.getIntegerInstance(locale);
                    nf.setGroupingUsed(true);

                    String formatted = nf.format(abs);
                    Character signChar = format.getSign().charFor(sign);

                    String prefix = signChar != null ? String.valueOf(signChar) : null;

                    return alignNumber(
                            alignmentOrDefault(format, arg),
                            format.getWidth(),
                            prefix,
                            formatted
                    );
                }

                // Existing logic for other types (b, o, x, etc.)
                int radix = switch (type) {
                    case 'b' -> 2;
                    case 'o' -> 8;
                    case 'x', 'X' -> 16;
                    default -> 10;
                };

                String digits = abs.toString(radix);
                if (type == 'X') digits = digits.toUpperCase();

                String groupChar = Optional.ofNullable(format.getIntPartGrouping())
                        .map(String::valueOf).orElse(null);

                int groupSize = switch (radix) {
                    case 2, 8, 16 -> 4;
                    default -> 3;
                };

                if (groupChar != null) {
                    digits = Utils.formatIntGrouped(digits, groupSize, groupChar);
                }

                String prefix = "";
                if (format.isSpecial()) {
                    prefix = switch (radix) {
                        case 16 -> type == 'X' ? "0X" : "0x";
                        case 2 -> "0b";
                        case 8 -> "0o";
                        default -> "";
                    };
                }

                Character signChar = format.getSign().charFor(sign);
                if (signChar != null) {
                    prefix = signChar + prefix;
                }

                return alignNumber(
                        alignmentOrDefault(format, arg),
                        format.getWidth(),
                        prefix,
                        digits
                );
            };


    public static final ArgumentConverter<BigDecimal, String> BIG_DECIMAL_CONVERTER =
            (ctx, arg, format) -> {

                char type = format.getType();
                int sign = arg.signum();
                BigDecimal abs = arg.abs();

                Integer precision = format.getPrecision();
                if (precision == null) precision = 6;

                Character signChar = format.getSign().charFor(sign);
                String signStr = signChar == null ? "" : String.valueOf(signChar);

                String formatted;

                // N-mode: locale-aware formatting
                if (type == 'n' || type == 'N') {
                    Locale locale = contextLanguage(ctx);
                    if (locale == null) locale = Locale.ROOT;

                    // Use java.text.NumberFormat for locale-aware formatting
                    NumberFormat nf = NumberFormat.getNumberInstance(locale);
                    nf.setGroupingUsed(true);
                    nf.setMinimumFractionDigits(type == 'N' ? nf.getMinimumFractionDigits() : 0);
                    nf.setMaximumFractionDigits(precision);

                    formatted = nf.format(abs);

                } else {
                    switch (type) {
                        case 'f', 'F' -> {
                            formatted = abs
                                    .setScale(precision, RoundingMode.HALF_UP)
                                    .toPlainString();
                            if (type == 'F') formatted = formatted.toUpperCase();
                        }

                        case 'e', 'E' -> {
                            BigDecimal scaled = abs.round(new MathContext(precision, RoundingMode.HALF_UP));
                            formatted = scaled.toEngineeringString();

                            if (!formatted.contains("E") && !formatted.contains("e")) {
                                formatted += "E+0";
                            }

                            if (type == 'E') formatted = formatted.toUpperCase();
                        }

                        case '%' -> {
                            BigDecimal pct = abs.multiply(BigDecimal.valueOf(100));
                            formatted = pct
                                    .setScale(precision, RoundingMode.HALF_UP)
                                    .toPlainString() + "%";
                        }

                        default -> formatted = abs.toPlainString();
                    }

                    // grouping integer part for non-locale formats
                    if (format.getIntPartGrouping() != null && !formatted.contains("E") && !formatted.contains("e")) {
                        String[] parts = formatted.split("\\.", 2);
                        String intPart = Utils.formatIntGrouped(
                                parts[0],
                                3,
                                format.getIntPartGrouping().toString()
                        );
                        formatted = parts.length == 2 ? intPart + "." + parts[1] : intPart;
                    }
                }

                // '#' — remove trailing .0
                if (format.isSpecial()) {
                    char decimalSep = '.';
                    if (type == 'n' || type == 'N') {
                        Locale locale = contextLanguage(ctx);
                        if (locale == null) locale = Locale.ROOT;
                        var symbols = DecimalFormatSymbols.getInstance(locale);
                        decimalSep = symbols.getDecimalSeparator();
                    }

                    int sepIndex = formatted.indexOf(decimalSep);
                    if (sepIndex >= 0 && Utils.fractionalPartIsZero(formatted, sepIndex)) {
                        formatted = formatted.substring(0, sepIndex);
                        if (type == '%') formatted += "%";
                    }
                }

                return alignNumber(
                        alignmentOrDefault(format, arg),
                        format.getWidth(),
                        signStr,
                        formatted
                );
            };



    public static <T> String format(ArgumentConverter<T, String> conv, FormatPattern format, T value, boolean repr) {
        var ctx = new Context(); // create empty context
        if (repr) {
            var s = Utils.quote(String.valueOf(value));
            return STRING_CONVERTER.format(ctx, s, format);
        }
        return conv.format(ctx, value, format);
    }

    public static <T> String format(ArgumentConverter<T, String> conv, String format, T value, boolean repr) {
        var f = FormatPattern.parse(format);
        return format(conv, f, value, repr);
    }

    public static <T> String format(ArgumentConverter<T, String> conv, String format, T value) {
        return format(conv, format, value, false);
    }

}
