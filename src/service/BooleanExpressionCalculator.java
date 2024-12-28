package service;

import util.Parser;
import util.Tokenizer;

public class BooleanExpressionCalculator {

    // Method to evaluate a boolean expression
    public static boolean evaluate(String expression) throws Exception {
        expression = expression.replaceAll("true","1>0");
        expression = expression.replaceAll("false","1<0");


        // Create a Tokenizer instance to tokenize the expression
        Tokenizer tokenizer = new Tokenizer(expression);

        // Create a Parser instance to parse the tokenized expression
        Parser parser = new Parser(tokenizer);

        // Parse the expression and return the evaluated result (boolean)
        return parser.parseExpression();
    }
}
