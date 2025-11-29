package xyz.bobkinn.indigoi18n.template;

public class Utils {
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

    public static String formatIntGrouped(
            String value,
            int groupSize,
            String separator
    ) {
        // No grouping requested
        if (separator == null || groupSize <= 0) {
            return value;
        }

        // Reverse for easier chunking from the end
        StringBuilder sb = new StringBuilder(value).reverse();
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

    public static String quote(String s) {
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

    public static boolean fractionalPartIsZero(String s, int dotIndex) {
        int i = dotIndex + 1;

        // Skip optional sign or percent at the end
        while (i < s.length()) {
            char c = s.charAt(i);
            if (c >= '0' && c <= '9') {
                if (c != '0') {
                    return false; // non-zero digit found
                }
            } else if (c == '%') {
                // ignore % and stop
                return true;
            }
            i++;
        }

        // No non-zero digits found
        return true;
    }
}
