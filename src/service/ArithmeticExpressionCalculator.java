package service;

import java.util.HashMap;
import java.util.Stack;
import java.util.function.BiFunction;

public class ArithmeticExpressionCalculator {
    private static final HashMap<String, BiFunction<Integer, Integer, Integer>> operationsMap;
    static {
        operationsMap = new HashMap<>();
        operationsMap.put("+", Integer::sum);
        operationsMap.put("-", (x, y) -> x - y);
        operationsMap.put("*", (x, y) -> x * y);
        operationsMap.put("%", (x, y) -> {
            if (y == 0) throw new ArithmeticException("Division by zero");
            return x % y;
        });
        operationsMap.put("/", (x, y) -> {
            if (y == 0) throw new ArithmeticException("Division by zero");
            return x / y;
        });
    }

    // Method to evaluate an expression
    public static int evaluateExpression(String expression) throws Exception {
        expression = expression.replaceAll("\\s+", "");  // Remove all whitespace
        if (expression.isEmpty()) throw new Exception("Expression is empty");
        return evaluate(expression);
    }

    // Helper method to evaluate the expression using a stack
    private static int evaluate(String expression) throws Exception {
        Stack<Integer> values = new Stack<>();
        Stack<Character> operators = new Stack<>();

        for (int i = 0; i < expression.length(); i++) {
            char current = expression.charAt(i);

            // If the current character is a number
            if (Character.isDigit(current)) {
                int value = 0;
                while (i < expression.length() && Character.isDigit(expression.charAt(i))) {
                    value = value * 10 + (expression.charAt(i) - '0');
                    i++;
                }
                i--; // We move one extra position in the loop, so move back one step.
                values.push(value);
            }
            // If the current character is a '('
            else if (current == '(') operators.push(current);

            // If the current character is a ')'
            else if (current == ')') {
                while (operators.peek() != '(') {
                    values.push(applyOperation(operators.pop(), values.pop(), values.pop()));
                }
                operators.pop(); // Pop the '(' from the stack
            }
            // If the current character is an operator
            else if (operationsMap.containsKey(current + "")) {
                while (!operators.isEmpty() && precedence(current) <= precedence(operators.peek())) {
                    values.push(applyOperation(operators.pop(), values.pop(), values.pop()));
                }
                operators.push(current);
            }
        }

        // Apply remaining operations
        while (!operators.isEmpty()) values.push(applyOperation(operators.pop(), values.pop(), values.pop()));

        // The result is the last value in the stack
        return values.pop();
    }

    // Method to apply an operation to two numbers
    private static int applyOperation(char operator, int b, int a) throws Exception {
        BiFunction<Integer, Integer, Integer> operation = operationsMap.get(operator+"");
        if (operation == null) throw new Exception("Invalid operator");
        return operation.apply(a, b);
    }

    // Method to determine precedence of operators
    private static int precedence(char operator) {
        if (operator == '+' || operator == '-') return 1;
        if (operator == '*' || operator == '/' || operator == '%') return 2;
        return -1;
    }
}
