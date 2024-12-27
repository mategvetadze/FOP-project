package service;

import service.exeption.InvalidOutput;  // Importing a custom exception class

import java.util.Scanner;

public class InputOutput {

    private Interpreter interpreter;  // Interpreter object for handling variables and expressions
    private static final Scanner scanner = new Scanner(System.in);  // Scanner to read user input

    // Constructor that accepts an Interpreter object for use in this class
    public InputOutput(Interpreter interpreter) {
        this.interpreter = interpreter;  // Initialize the interpreter
    }

    // Method to handle inputs and process variables
    public void inputHandler(String Line) {
        // Remove all spaces from the input line
        Line = Line.replaceAll(" ", "");
        int n = Line.length();  // Get the length of the input string

        // Loop through each character in the string
        for (int k = 0; k < n; k++) {
            // Check if the character is '&', indicating a variable declaration
            if (Line.charAt(k) == '&') {
                StringBuilder s = new StringBuilder();  // StringBuilder to build the variable name
                k++;  // Move past the '&' character
                // Loop until we reach a comma or closing parenthesis
                while (Line.charAt(k) != ',' && Line.charAt(k) != ')') {
                    s.append(Line.charAt(k));  // Append each character to the variable name
                    k++;
                }
                // Add the variable to the interpreter using the variable name and the next integer from input
                interpreter.addVariable(s.toString(), scanner.nextInt());
            }
        }
    }

    // Method to handle outputs and print the result
    public void outputHandler(String Line) throws InvalidOutput {
        // Remove all spaces from the input line
        Line = Line.replaceAll(" ", "");
        int n = Line.length();  // Get the length of the input string

        int ExceptionCnt = 0;  // Counter to track exceptions if multiple conversions fail
        int ind1 = Line.indexOf('('), ind2 = Line.lastIndexOf(')');  // Find the positions of parentheses
        String Output = Line.substring(ind1 + 1, ind2);  // Extract the output content inside parentheses

        // Check if there is a single '"' character, which indicates an invalid output
        ind1 = Output.indexOf('"');
        ind2 = Output.lastIndexOf('"');
        if (ind1 == ind2 && ind1 != -1) {
            throw new InvalidOutput();  // Throw InvalidOutput exception if single '"' is found
        }
        // If there's a '"' in the output, print the string between them
        else if (ind1 != -1) {
            System.out.print(Output.substring(ind1 + 1, ind2));
        } else {
            ExceptionCnt++;  // Increment exception count if no string is found
            try {
                // Try converting the output to an integer and print
                System.out.print(String_ToInt(Output));
            } catch (Exception e) {
                ExceptionCnt++;  // Increment exception count if conversion to integer fails
                try {
                    // Try converting the output to a boolean and print
                    System.out.print(String_ToBool(Output));
                } catch (Exception b) {
                    ExceptionCnt++;  // Increment exception count if conversion to boolean fails
                }
            }
        }
        // If all conversion attempts failed, throw an InvalidOutput exception
        if (ExceptionCnt == 3) throw new InvalidOutput();
            // If the line contains "Println", print a newline character
        else if (Line.contains("Println")) {
            System.out.println();
        }
    }

    // Method to convert a string to an integer using the interpreter
    public Integer String_ToInt(String s) {
        return (int) interpreter.getVariableTypes("int").apply(s);
    }

    // Method to convert a string to a boolean using the interpreter
    public Boolean String_ToBool(String s) {
        return (boolean) interpreter.getVariableTypes("bool").apply(s);
    }
}
