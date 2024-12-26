package service;

import util.Parser;
import util.Tokenizer;

import java.util.Scanner;

public class BooleanExpressionCalculator {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter a boolean expression in Go syntax:");
        String input = scanner.nextLine();

        try {
            boolean result = evaluate(input);
            System.out.println("Result: " + result);
        } catch (Exception e) {
            System.out.println("Invalid expression: " + e.getMessage());
        }
    }

    // Entry point for evaluating the expression
    public static boolean evaluate(String expression) throws Exception {
        Tokenizer tokenizer = new Tokenizer(expression);
        Parser parser = new Parser(tokenizer);
        return parser.parseExpression();
    }
}

