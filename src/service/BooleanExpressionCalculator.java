package service;

import util.Parser;
import util.Tokenizer;

import java.util.Scanner;

public class BooleanExpressionCalculator {

    public static boolean evaluate(String expression) throws Exception {
        Tokenizer tokenizer = new Tokenizer(expression);
        Parser parser = new Parser(tokenizer);
        return parser.parseExpression();
    }
}

