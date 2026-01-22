package xyz.bobkinn.indigoi18n.template.format;

import lombok.Data;
import xyz.bobkinn.indigoi18n.template.TemplateReader;

@Data
public class FormatPattern {
    private final String source;
    private final Alignment alignment;
    private final Sign sign;
    private final boolean alternate;
    private final Integer width;
    private final Integer precision;
    private final Character intPartGrouping;
    private final char type;

    private static Character readOptionalGrouping(TemplateReader reader) {
        if (reader.tryConsume(',')) {
            return ',';
        } else if (reader.tryConsume('_')) {
            return '_';
        } else return null;
    }

    public static FormatPattern parse(String text) {
        return readFormatSpec(new TemplateReader(text));
    }

    public static FormatPattern newDefault() {
        return new FormatPattern("", null,
                Sign.NEGATIVE, false, null, null, null, 's');
    }

    public static FormatPattern readFormatSpec(TemplateReader reader) {
        reader.pushMark(); // make a mark
        var alignment = Alignment.read(reader);
        var sign = Sign.read(reader);
        boolean special = reader.tryConsume('#');
        if (reader.tryConsume('0')) {
            if (alignment != null && alignment.fill == null) {
                // add filling
                alignment = new Alignment(alignment.type, ' ');
            } else if (alignment == null) {
                alignment = new Alignment(AlignType.SIGN, ' ');
            }
        }
        Integer width;
        if (reader.hasUnsignedNumber()) {
            width = reader.readUnsignedNumber();
        } else {
            width = null;
        }
        Character intPartGrouping = readOptionalGrouping(reader);
        Integer precision;
        if (reader.tryConsume('.')) {
            precision = reader.readUnsignedNumber();
        } else {
            precision = null;
        }
        char type;
        if (reader.hasNext() && reader.peek() != '}') {
            type = reader.next();
        } else type = 's';
        var source = reader.markedPart();
        reader.popMark(false); // pop mark without resetting to its pos
        return new FormatPattern(source, alignment, sign, special, width, precision, intPartGrouping, type);
    }

    public static String asString(Object arg, FormatPattern pattern) {
        return "%%{%s:%s}".formatted(arg, pattern.getSource());
    }

    public enum AlignType {
        TO_LEFT,
        TO_RIGHT,
        CENTER,
        /**
         * -23 -> -0023 (width 5, fill '0'). {@code :0=5}
         */
        SIGN;

        public static AlignType read(TemplateReader reader, boolean consume, int offset) {
            if (!reader.hasNext(offset)) {
                return null;
            }
            var ch = reader.peek(offset);
            final AlignType ret;
            if (ch == '<') {
                ret = AlignType.TO_LEFT;
            } else if (ch == '>') {
                ret = AlignType.TO_RIGHT;
            } else if (ch == '^') {
                ret = AlignType.CENTER;
            } else if (ch == '=') {
                ret = AlignType.SIGN;
            } else {
                ret = null;
            }
            if (ret != null && consume) {
                reader.skip();
            }
            return ret;
        }
    }

    public enum Sign {
        BOTH,
        NEGATIVE,
        /**
         * {@code _ 234} from {@code 234:_> 5} (align right, width 5)<br>
         * Like BOTH, but plus is replaced with space
         */
        SPACE_POSITIVE;

        public static Sign read(TemplateReader reader) {
            if (reader.tryConsume('+')) return BOTH;
            if (reader.tryConsume('-')) return NEGATIVE;
            if (reader.tryConsume(' ')) return SPACE_POSITIVE;
            return Sign.NEGATIVE; // default
        }

        public Character charFor(int sign) {
            return switch (this) {
                case BOTH -> sign >= 0 ? '+' : '-';
                case NEGATIVE -> sign < 0 ? '-' : null;
                case SPACE_POSITIVE -> sign >= 0 ? ' ' : '-';
            };
        }
    }

    /**
     * @param fill fill character. null means not provided in parse-time
     */
    public record Alignment(AlignType type, Character fill) {

        public String repeatFill(int count) {
            if (fill == null) return  " ".repeat(count);
            return fill.toString().repeat(count);
        }

        public static Alignment read(TemplateReader reader) {
            var align1 = AlignType.read(reader, false, 0);
            var align2 = AlignType.read(reader, false ,1);
            if (align1 != null && align2 != null) {
                // handle something like <<
                // in this case it is always fill+align because symbols after align cant be ><^=
                var fill = reader.next(); // any symbol
                reader.skip(); // consume align
                return new Alignment(align2, fill);
            }
            if (align1 == null && align2 != null) {
                // handle normal a<
                var fill = reader.next(); // any symbol
                reader.skip(); // consume align
                return new Alignment(align2, fill);
            }
            if (align1 != null) {
                reader.skip(); // consume align
                // first align is parsed and second is not so its just <
                return new Alignment(align1, null);
            }
            return null;
        }
    }
}
