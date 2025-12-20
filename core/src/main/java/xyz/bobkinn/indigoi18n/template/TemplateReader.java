package xyz.bobkinn.indigoi18n.template;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.Deque;

public class TemplateReader {
    private final String text;
    private int next = 0;

    private final Deque<Integer> markStack = new ArrayDeque<>();

    public TemplateReader(String text) {
        this.text = text;
    }

    /**
     * Pushes the current read position onto the mark stack.
     */
    public void pushMark() {
        markStack.push(next);
    }

    /**
     * Pops the top mark from the stack and moves the reading position back to it if set arg set to true.
     * @throws IllegalStateException if the mark stack is empty
     */
    public void popMark(boolean set) {
        if (markStack.isEmpty()) {
            throw new IllegalStateException("Mark stack is empty");
        }
        var idx = markStack.pop();
        if (set) next = idx;
    }

    /**
     * Returns the text from the top mark (most recent pushMark) to the current position.
     * @throws IllegalStateException if mark stack is empty
     */
    public String markedPart() {
        if (markStack.isEmpty()) {
            throw new IllegalStateException("Mark stack is empty");
        }
        int topMark = markStack.peek();
        if (topMark > next) {
            throw new IllegalStateException("Top mark is bigger than next");
        }
        return text.substring(topMark, next);
    }

    public boolean hasNext() {
        return next < text.length();
    }

    public boolean hasNext(int offset) {
        return next +offset < text.length() && next + offset >= 0;
    }

    public char next() {
        if (!hasNext()) {
            throw new IllegalStateException("No more characters");
        }
        return text.charAt(next++);
    }

    public boolean hasUnsignedNumber() {
        return hasNext() && Character.isDigit(peek());
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
        next++;
    }

    public char peek() {
        return peek(0);
    }

    public char peek(int offset) {
        int idx = next + offset;
        if (idx < 0 || idx >= text.length()) {
            throw new IllegalArgumentException(
                    "Cannot peek character offset by " + offset + " symbols"
            );
        }
        return text.charAt(idx);
    }

    /**
     * Reads characters from the current position up to (but not including) the specified delimiter.
     * <p>
     * If {@code requireDelimiter} is {@code true}, the method throws an {@link IllegalStateException}
     * if the delimiter is not found. If {@code requireDelimiter} is {@code false}, the method
     * returns all remaining characters up to the end of the text when the delimiter is not found.
     * <p>
     * The {@code next} position is updated as follows:
     * <ul>
     *     <li>If the delimiter is found, {@code next} points to the first character of the delimiter.</li>
     *     <li>If the delimiter is not found and {@code requireDelimiter} is {@code false}, {@code next} points to the end of the text.</li>
     * </ul>
     *
     * @param delimiter the string to read until; must not be empty
     * @param requireDelimiter if {@code true}, throws an exception if the delimiter is not found;
     *                         if {@code false}, returns the rest of the text when delimiter is absent
     * @return the substring from the current position up to (but not including) the delimiter,
     *         or the remaining text if the delimiter is not found and {@code requireDelimiter} is {@code false}
     * @throws IllegalArgumentException if {@code delimiter} is empty
     * @throws IllegalStateException if {@code requireDelimiter} is {@code true} and the delimiter is not found
     */

    public String readUntil(@NotNull String delimiter, boolean requireDelimiter) {
        if (delimiter.isEmpty()) {
            throw new IllegalArgumentException("Delimiter cannot be empty");
        }

        int start = next;
        int idx = text.indexOf(delimiter, next);
        if (requireDelimiter) {
            if (idx == -1) {
                throw new IllegalStateException("Delimiter '" + delimiter + "' not found");
            }
            next = idx; // move next to the start of delimiter
        } else {
            // use entire string if delimiter not found
            next = text.length();
        }

        return text.substring(start, next);
    }
}

