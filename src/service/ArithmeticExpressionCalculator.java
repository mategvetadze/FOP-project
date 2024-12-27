package service;

import java.util.HashMap;
import java.util.Stack;
import java.util.function.BiFunction;

public class ArithmeticExpressionCalculator {

    // A static map to store operators and their corresponding operations
    private static final HashMap<String, BiFunction<Integer, Integer, Integer>> operationsMap;

    // Static block to initialize the operationsMap with arithmetic operations
    static {
        operationsMap = new HashMap<>();
        operationsMap.put("+", Integer::sum); // Addition operation
        operationsMap.put("-", (x, y) -> x - y); // Subtraction operation
        operationsMap.put("*", (x, y) -> x * y); // Multiplication operation
        operationsMap.put("%", (x, y) -> { // Modulus operation with division by zero check
            if (y == 0) throw new ArithmeticException("Division by zero");
            return x % y;
        });
        operationsMap.put("/", (x, y) -> { // Division operation with division by zero check
            if (y == 0) throw new ArithmeticException("Division by zero");
            return x / y;
        });
    }

    // Method to evaluate an arithmetic expression
    public static int evaluateExpression(String expression) throws Exception {
        // Remove all whitespaces from the expression
        expression = expression.replaceAll("\\s+", "");

        // Check if the expression is empty
        if (expression.isEmpty()) throw new Exception("Expression is empty");

        // Call the evaluate method to compute the result
        return evaluate(expression);
    }

    // Helper method to evaluate the expression using two stacks: one for values and one for operators
    private static int evaluate(String expression) throws Exception {
        // Stack to store numbers
        Stack<Integer> values = new Stack<>();

        // Stack to store operators
        Stack<Character> operators = new Stack<>();

        // Loop through each character in the expression
        for (int i = 0; i < expression.length(); i++) {
            char current = expression.charAt(i);

            // If the current character is a digit, process the number
            if (Character.isDigit(current)) {
                int value = 0;
                // While we encounter a digit, build the number
                while (i < expression.length() && Character.isDigit(expression.charAt(i))) {
                    value = value * 10 + (expression.charAt(i) - '0');
                    i++;
                }
                i--; // Move one step back since the loop increments 'i' at the end
                values.push(value); // Push the number onto the values stack
            }
            // If the current character is '(', push it to the operators stack
            else if (current == '(') operators.push(current);

                // If the current character is ')', process the operators until '(' is found
            else if (current == ')') {
                // Process operators within the parentheses
                while (operators.peek() != '(') {
                    values.push(applyOperation(operators.pop(), values.pop(), values.pop()));
                }
                operators.pop(); // Pop the '(' from the stack
            }
            // If the current character is an operator, process it
            else if (operationsMap.containsKey(current + "")) {
                // While the operator at the top of the stack has higher or equal precedence, apply the operation
                while (!operators.isEmpty() && precedence(current) <= precedence(operators.peek())) {
                    values.push(applyOperation(operators.pop(), values.pop(), values.pop()));
                }
                operators.push(current); // Push the current operator onto the stack
            }
        }

        // Apply remaining operations in the stack
        while (!operators.isEmpty()) values.push(applyOperation(operators.pop(), values.pop(), values.pop()));

        // The result is the only number left in the stack
        return values.pop();
    }

    // Method to apply an operation to two operands
    private static int applyOperation(char operator, int b, int a) throws Exception {
        // Retrieve the operation corresponding to the operator
        BiFunction<Integer, Integer, Integer> operation = operationsMap.get(operator + "");

        // If the operator is not valid, throw an exception
        if (operation == null) throw new Exception("Invalid operator");

        // Apply the operation to the operands and return the result
        return operation.apply(a, b);
    }

    // Method to determine the precedence of operators
    private static int precedence(char operator) {
        if (operator == '+' || operator == '-') return 1; // Low precedence
        if (operator == '*' || operator == '/' || operator == '%') return 2; // High precedence
        return -1; // Invalid operator
    }
}
