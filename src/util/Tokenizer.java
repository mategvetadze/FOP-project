package util;

// Tokenizer class to break the expression into tokens
public class Tokenizer {
    private final String input;
    private int pos;

    public Tokenizer(String input) {
        this.input = input.replaceAll("\\s+", ""); // Remove spaces
        this.pos = 0;
    }

    public String peek() {
        if (pos >= input.length()) return null;

        // Comparison operators
        if (input.startsWith(">=", pos)) return ">=";
        if (input.startsWith("<=", pos)) return "<=";
        if (input.startsWith("==", pos)) return "==";
        if (input.startsWith("!=", pos)) return "!=";
        if (input.startsWith(">", pos)) return ">";
        if (input.startsWith("<", pos)) return "<";

        // Boolean operators
        if (input.startsWith("&&", pos)) return "&&";
        if (input.startsWith("||", pos)) return "||";
        if (input.startsWith("!", pos)) return "!";

        // Literals and parentheses
        if (input.startsWith("true", pos)) return "true";
        if (input.startsWith("false", pos)) return "false";
        if (input.charAt(pos) == '(' || input.charAt(pos) == ')') return String.valueOf(input.charAt(pos));

        // Numbers
        if (Character.isDigit(input.charAt(pos))) {
            int end = pos;
            while (end < input.length() && (Character.isDigit(input.charAt(end)) || input.charAt(end) == '.')) {
                end++;
            }
            return input.substring(pos, end);
        }

        return null;
    }

    public String next() {
        String token = peek();
        if (token == null) throw new IllegalStateException("Unexpected token at position " + pos);
        pos += token.length();
        return token;
    }

    public boolean hasNext() {
        return peek() != null;
    }
}
