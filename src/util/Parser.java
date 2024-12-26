package util;


import java.util.HashMap;
import java.util.Objects;
import java.util.function.BiFunction;

// Parser class to evaluate the boolean expression
public class Parser {
    private final Tokenizer tokenizer;
    private static final HashMap<String, BiFunction<Double,Double, Boolean>> operatorsMap;
    static {
        operatorsMap = new HashMap<>();
        operatorsMap.put(">", (x,y)->x>y);
        operatorsMap.put("<", (x,y)->x<y);
        operatorsMap.put(">=", (x,y)->x>=y);
        operatorsMap.put("<=", (x,y)->x<=y);
        operatorsMap.put("==", Objects::equals);
        operatorsMap.put("!=", (x,y)-> !Objects.equals(x, y));
    }

    public Parser(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    public boolean parseExpression() {
        return parseOr();
    }

    private boolean parseOr() {
        boolean value = parseAnd();
        while (tokenizer.hasNext() && tokenizer.peek().equals("||")) {
            tokenizer.next(); // Consume ||
            value = value || parseAnd();
        }
        return value;
    }

    private boolean parseAnd() {
        boolean value = parseComparison();
        while (tokenizer.hasNext() && tokenizer.peek().equals("&&")) {
            tokenizer.next(); // Consume &&
            value = value && parseComparison();
        }
        return value;
    }

    private boolean parseComparison() {
        double left = parseFactor();
        if (tokenizer.hasNext()) {
            String operator = tokenizer.peek();
                tokenizer.next(); // Consume operator
                double right = parseFactor();
                return operatorsMap.get(operator).apply(left, right);
        }
        return left != 0; // Convert numeric result to boolean
    }

    private double parseFactor() {
        if (tokenizer.peek().equals("!")) {
            tokenizer.next(); // Consume !
            return parseFactor() == 0 ? 1 : 0; // Negate the boolean value
        } else if (tokenizer.peek().equals("(")) {
            tokenizer.next(); // Consume (
            double value = parseExpression() ? 1 : 0; // Treat the result as boolean
            if (!tokenizer.next().equals(")")) {
                throw new IllegalStateException("Missing closing parenthesis");
            }
            return value;
        } else if (tokenizer.peek().equals("true")) {
            tokenizer.next(); // Consume true
            return 1; // true as numeric 1
        } else if (tokenizer.peek().equals("false")) {
            tokenizer.next(); // Consume false
            return 0; // false as numeric 0
        } else {
            // Numeric literal
            try {
                return Double.parseDouble(tokenizer.next());
            } catch (NumberFormatException e) {
                throw new IllegalStateException("Invalid number format");
            }
        }
    }
}
