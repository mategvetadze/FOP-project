package service;

import service.exeption.SyntaxException;

import java.util.Scanner;

public class InputOutput {
    private Interpreter interpreter;
    private static final Scanner scanner = new Scanner(System.in);

    public InputOutput(Interpreter interpreter) {
        this.interpreter = interpreter;

    }

    public void inputHandler(String Line) {
        Line.replaceAll(" ", "");
        int n = Line.length();
        for (int k = 0; k < n; k++) {
            if (Line.charAt(k) == '&') {
                StringBuilder s = new StringBuilder();
                k++;
                while (k<n&&(Line.charAt(k) != ',' && Line.charAt(k) != ')')) {
                    s.append(Line.charAt(k));
                    k++;
                }
                interpreter.addVariable(s.toString(), scanner.nextInt());
            }
        }
    }

    public void outputHandler(String Line) throws SyntaxException {
        Line.replaceAll(" ", "");
        int n = Line.length();
        if (Line.contains("Println")){
            int ind1=Line.indexOf('('), ind2=Line.lastIndexOf(')');
            String s=Line.substring(ind1,ind2);
            try {
                BooleanExpressionCalculator.evaluate(s);
            }catch (Exception e){
                throw new SyntaxException();
            }

        }
    }
    public static void main(String[] args) {

    }
}
