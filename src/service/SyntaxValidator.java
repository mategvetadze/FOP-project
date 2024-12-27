package service;
import java.util.Stack;
import java.util.ArrayList;

public class SyntaxValidator {

    public static void main(String[] args) {
        String code = """
                package service               

                import "fmt"

                func main() 
                { 
                    Var a int
                    fmt.Scan(&a)
                    var reversed int
                    var copyOfA int = a
                    for copyOfA > 0 
                    {
                        reversed = reversed*10 + copyOfA%10
                        copyOfA = copyOfA / 10
                    }
                    if (reversed == 0) {
                        reversed = 1
                    }
                    fmt.Println(a = reversed)
                }
                """; // Put your Go code here
        checkSyntax(code);
    }

    public static void checkSyntax(String code) {
        Stack<Character> stack = new Stack<>();
        ArrayList<String> errors = new ArrayList<>();  // List to collect errors
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

            // Check for non-English alphabet characters
            if (!trimmedLine.matches("[\\x00-\\x7F]*")) {
                errors.add("Error: Non-English characters detected in the code.");
            }

            // Check for semicolons (they are not allowed in Go)
            if (trimmedLine.contains(";")) {
                errors.add("Error: Semicolons are not allowed in Go.");
            }

            // Check for package declaration
            if (trimmedLine.startsWith("package")) {
                hasPackage = true;
                // Validate package name
                if (!trimmedLine.contains(packageName)) {
                    errors.add("Error: Expected package name 'service', found: " + trimmedLine);
                }
            }

            // Check for import statement (case-insensitive check for "fmt")
            else if (trimmedLine.toLowerCase().startsWith("import")) {
                hasImport = true;
                if (!trimmedLine.toLowerCase().contains("fmt")) {
                    errors.add("Error: Missing import of 'fmt'.");
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
                    errors.add("Error: Invalid syntax in 'if' condition (variable declaration or assignment within).");
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
                    errors.add("Error: Invalid syntax in 'for' loop (variable declaration or assignment within).");
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
                        errors.add("Error: Mismatched closing curly brace.");
                    } else {
                        stack.pop();
                    }
                }
            }

            // Skip assignment check for variable declarations (e.g., var copyOfA int = a)
            else if (trimmedLine.contains("=") && !trimmedLine.startsWith("var")) {
                // Check for assignment syntax
                checkAssignmentSyntax(trimmedLine, errors);
            }

            // Handle variable declaration (var keyword)
            else if (trimmedLine.toLowerCase().startsWith("var")) {
                inVarDeclaration = true;
                checkVarDeclaration(trimmedLine, errors);
            }

            // Handle function call
            else if (trimmedLine.contains("(") && trimmedLine.contains(")")) {
                inFuncCall = true;
                checkFunctionCall(trimmedLine, errors);
            }

            // Handle unknown keyword or unknown syntax
            else {
                errors.add("Error: Unknown syntax or incorrect keyword: " + trimmedLine);
            }

            // Reset state after processing a line
            inAssignment = false;
            inVarDeclaration = false;
            inFuncCall = false;
        }

        // Check if there are unmatched curly braces
        if (!stack.isEmpty()) {
            errors.add("Error: Unmatched opening curly brace.");
        }

        // Ensure that package and import are present
        if (!hasPackage) {
            errors.add("Error: Missing 'package' declaration.");
        }
        if (!hasImport) {
            errors.add("Error: Missing 'import' declaration.");
        }

        // Output all errors
        if (errors.isEmpty()) {
            System.out.println("No syntax errors detected.");
        } else {
            for (String error : errors) {
                System.out.println(error);
            }
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

    private static void checkAssignmentSyntax(String line, ArrayList<String> errors) {
        // Skip assignment checks for variable declarations like 'var x int = 5'
        if (line.startsWith("var") && line.contains("=")) {
            // This is a valid declaration with assignment, so skip validation here
            return;
        }

        // Check for invalid assignment in fmt.Println or other function calls
        if (line.contains("=") && line.contains("fmt.Println")) {
            errors.add("Error: Invalid assignment inside fmt.Println() function call.");
            return;
        }

        // Handle regular assignment (e.g., a = 5)
        String[] parts = line.split("=");
        if (parts.length != 2 || parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) {
            errors.add("Error: Invalid assignment syntax.");
        }
    }

    private static void checkVarDeclaration(String line, ArrayList<String> errors) {
        // Trim the line and convert it to lowercase for consistent comparison
        String trimmedLine = line.trim();

        // Check for incorrect capitalization of 'var'
        if (trimmedLine.startsWith("Var") || trimmedLine.startsWith("VAR")) {
            errors.add("Error: 'Var' is not valid. Use 'var' for variable declaration.");
            return;
        }

        // Check if the line starts with the 'var' keyword
        if (!trimmedLine.startsWith("var")) {
            errors.add("Error: Invalid variable declaration.");
            return;
        }

        // Split the declaration into parts based on spaces
        String[] parts = trimmedLine.split("\\s+");

        // Ensure the declaration has at least 3 parts: 'var', variable name, and type
        if (parts.length < 3) {
            errors.add("Error: Invalid variable declaration (missing type or variable name).");
            return;
        }

        // Validate that the second part is a valid variable name (only letters or numbers, no special characters)
        if (!parts[1].matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
            errors.add("Error: Invalid variable name: " + parts[1]);
            return;
        }

        // Ensure the third part is a valid type (in this case, only simple types are expected, like int, string, etc.)
        if (!parts[2].matches("int|float64|string|bool")) {
            errors.add("Error: Invalid variable type: " + parts[2]);
            return;
        }

        // Handle cases where there's an assignment with a value
        if (parts.length > 3) {
            // If there's an equals sign, check for proper initialization
            String assignmentPart = trimmedLine.substring(trimmedLine.indexOf('=')).trim();

            // Check if the assignment part is empty (e.g., "var x int =")
            if (assignmentPart.isEmpty()) {
                errors.add("Error: Invalid assignment in variable declaration (missing value).");
            } else {
                // Check if there's a valid expression after the equals sign
                // This can be enhanced with more validation if necessary
                if (assignmentPart.split("\\s+").length < 1) {
                    errors.add("Error: Invalid syntax for value in variable declaration.");
                }
            }
        }
    }


    private static void checkFunctionCall(String line, ArrayList<String> errors) {
        if (!line.contains("(") || !line.contains(")")) {
            errors.add("Error: Invalid function call syntax.");
        }
    }
}
