package xyz.bobkinn.indigoi18n.template;

public class TemplateReader {
    private final String text;
    private int index = 0;

    public TemplateReader(String text) {
        this.text = text;
    }

    public boolean hasNext() {
        return index < text.length();
    }

    public boolean hasNext(int offset) {
        return index+offset < text.length() && index + offset >= 0;
    }

    public char next() {
        if (!hasNext()) {
            throw new IllegalStateException("No more characters");
        }
        return text.charAt(index++);
    }

    public boolean hasUnsignedNumber() {
        return Character.isDigit(peek());
    }

    public int readUnsignedNumber() {
        var sb = new StringBuilder();
        while (hasNext()) {
            var ch = peek();
            if (Character.isDigit(ch)) {
                skip();
                sb.append(ch);
            } else {
                break;
            }
        }
        if (sb.isEmpty()) {
            throw new IllegalStateException("No digits read");
        }
        return Integer.parseInt(sb.toString());
    }

    public void consume(char expectation) {
        var ch = next();
        if (ch != expectation) {
            throw new IllegalStateException("Expected "+expectation+" but found "+ch);
        }
    }

    /**
     * If peeked character is passed char, then next()
     * @return true if peeked character is next or false if character is different or cant read
     */
    public boolean tryConsume(char next) {
        if (!hasNext()) return false;
        if (peek() == next) {
            skip();
            return true;
        }
        return false;
    }

    public void skip() {
        if (!hasNext()) {
            throw new IllegalStateException("No more characters");
        }
        index++;
    }

    public char peek() {
        return peek(0);
    }

    public char peek(int offset) {
        int idx = index + offset;
        if (idx < 0 || idx >= text.length()) {
            throw new IllegalArgumentException(
                    "Cannot peek character offset by " + offset + " symbols"
            );
        }
        return text.charAt(idx);
    }
}

