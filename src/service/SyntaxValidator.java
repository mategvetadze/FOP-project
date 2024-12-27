package service;

import java.util.Stack;

public class SyntaxValidator {

    public static void main(String[] args) {
        String code = """
                package service               
                
                import "fmt"

                func main() 
                { 
                    var a int
                    fmt.Scan(&a)
                    var reversed int
                    var copyOfA int = a
                    for copyOfA > 0 {
                        reversed = reversed*10 + copyOfA%10
                        copyOfA = copyOfA / 10;
                    }
                    if (reversed == 0) {
                        reversed = 1
                    }
                    fmt.Println(a == reversed)
                }
                """; // Put your Go code here
        checkSyntax(code);
    }

    public static void checkSyntax(String code) {
        Stack<Character> stack = new Stack<>();
        boolean inCondition = false;
        boolean inAssignment = false;
        boolean inVarDeclaration = false;
        boolean inFuncCall = false;

        String[] lines = code.split("\n");

        boolean hasPackage = false;
        boolean hasImport = false;

        // Track package name
        String packageName = "service";  // Expected package name for validation

        boolean isInForLoop = false;
        boolean isInIfStatement = false;
        boolean isInFunction = false;  // To track function declarations

        for (String line : lines) {
            String trimmedLine = line.trim();

            // Skip empty lines
            if (trimmedLine.isEmpty()) {
                continue;
            }

            // Check for semicolons (they are not allowed in Go)
            if (trimmedLine.contains(";")) {
                System.out.println("Error: Semicolons are not allowed in Go.");
                return; // Exit after reporting the error
            }

            // Check for package declaration
            if (trimmedLine.startsWith("package")) {
                hasPackage = true;
                // Validate package name
                if (!trimmedLine.contains(packageName)) {
                    System.out.println("Error: Expected package name 'service', found: " + trimmedLine);
                }
            }

            // Check for import statement
            else if (trimmedLine.startsWith("import")) {
                hasImport = true;
                if (!trimmedLine.contains("fmt")) {
                    System.out.println("Error: Missing import of 'fmt'.");
                }
            }

            // Handle function declaration (e.g., func main() {)
            else if (trimmedLine.startsWith("func")) {
                isInFunction = true;
                // If there is an opening curly brace on the same line, treat it as valid
                if (trimmedLine.contains("{")) {
                    stack.push('{');
                }
            }

            // Handle "if" statement
            else if (trimmedLine.startsWith("if")) {
                if (inVarDeclaration || inAssignment) {
                    System.out.println("Error: Invalid syntax in 'if' condition (variable declaration or assignment within).");
                }
                inCondition = true;
                checkForParentheses(trimmedLine);
                // Check for curly brace, possibly on the same line
                if (trimmedLine.contains("{")) {
                    stack.push('{');
                }
                isInIfStatement = true;
            }

            // Handle "for" loop
            else if (trimmedLine.startsWith("for")) {
                if (inVarDeclaration || inAssignment) {
                    System.out.println("Error: Invalid syntax in 'for' loop (variable declaration or assignment within).");
                }
                checkForParentheses(trimmedLine);
                isInForLoop = true; // The for loop is being processed
                // Check for curly brace, possibly on the same line
                if (trimmedLine.contains("{")) {
                    stack.push('{');
                }
            }

            // Handle curly braces (either directly after "for", "if", or "func")
            else if (trimmedLine.equals("{") || trimmedLine.equals("}")) {
                if (trimmedLine.equals("{")) {
                    stack.push('{');
                } else if (trimmedLine.equals("}")) {
                    // Ensure the closing curly brace matches
                    if (stack.isEmpty()) {
                        System.out.println("Error: Mismatched closing curly brace.");
                    } else {
                        stack.pop();
                    }
                }
            }

            // Skip assignment check for variable declarations (e.g., var copyOfA int = a)
            else if (trimmedLine.contains("=") && !trimmedLine.startsWith("var")) {
                // Check for assignment syntax
                checkAssignmentSyntax(trimmedLine);
            }

            // Handle variable declaration (var keyword)
            else if (trimmedLine.startsWith("var")) {
                inVarDeclaration = true;
                checkVarDeclaration(trimmedLine);
            }

            // Handle function call
            else if (trimmedLine.contains("(") && trimmedLine.contains(")")) {
                inFuncCall = true;
                checkFunctionCall(trimmedLine);
            }

            // Handle unknown keyword or unknown syntax
            else {
                System.out.println("Error: Unknown syntax or incorrect keyword: " + trimmedLine);
            }

            // Reset state after processing a line
            inAssignment = false;
            inVarDeclaration = false;
            inFuncCall = false;
        }

        // Check if there are unmatched curly braces
        if (!stack.isEmpty()) {
            System.out.println("Error: Unmatched opening curly brace.");
        }

        // Ensure that package and import are present
        if (!hasPackage) {
            System.out.println("Error: Missing 'package' declaration.");
        }
        if (!hasImport) {
            System.out.println("Error: Missing 'import' declaration.");
        }
    }

    private static void checkForParentheses(String line) {
        int openParentheses = 0;
        int closeParentheses = 0;

        for (char c : line.toCharArray()) {
            if (c == '(') openParentheses++;
            if (c == ')') closeParentheses++;
        }

        if (openParentheses != closeParentheses) {
            System.out.println("Error: Mismatched parentheses in condition.");
        }
    }

    private static void checkAssignmentSyntax(String line) {
        // Skip assignment checks for variable declarations like 'var x int = 5'
        if (line.startsWith("var") && line.contains("=")) {
            // This is a valid declaration with assignment, so skip validation here
            return;
        }

        // Skip function calls or expressions with equals (e.g., fmt.Println(a == reversed))
        if (line.contains("fmt.Println") || line.contains("(") && line.contains(")")) {
            return;  // This is a function call, skip assignment validation
        }

        // Handle regular assignment (e.g., a = 5)
        String[] parts = line.split("=");
        if (parts.length != 2 || parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) {
            System.out.println("Error: Invalid assignment syntax.");
        }
    }

    private static void checkVarDeclaration(String line) {
        String[] parts = line.split(" ");
        if (parts.length < 3 || parts[0].equals("var") && parts[1].isEmpty()) {
            System.out.println("Error: Invalid variable declaration.");
        }
    }

    private static void checkFunctionCall(String line) {
        if (!line.contains("(") || !line.contains(")")) {
            System.out.println("Error: Invalid function call syntax.");
        }
    }
}
