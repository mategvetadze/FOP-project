package service;

import util.Parser;
import util.Tokenizer;

import java.util.Scanner;

public class BooleanExpressionCalculator {

    // Method to evaluate a boolean expression
    public static boolean evaluate(String expression) throws Exception {
        // Create a Tokenizer instance to tokenize the expression
        Tokenizer tokenizer = new Tokenizer(expression);

        // Create a Parser instance to parse the tokenized expression
        Parser parser = new Parser(tokenizer);

        // Parse the expression and return the evaluated result (boolean)
        return parser.parseExpression();
    }
}
