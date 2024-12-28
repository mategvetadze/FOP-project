package service;
import service.exception.SyntaxException;
import java.util.Stack;
import java.util.ArrayList;

/**
 * The {@code SyntaxValidator} class is responsible for validating the syntax of Go code.
 * It checks various aspects of Go code, including package declarations, imports, function definitions, variable declarations,
 * loops, conditional statements, and general syntax. It also verifies that the correct syntax is used throughout the code.
 * The syntax rules for Go, such as avoiding semicolons, using correct variable types, and matching parentheses and curly braces,
 * are strictly enforced by this validator.
 *
 * This class is used to ensure that Go code adheres to the expected structure and helps identify common syntax errors.
 * If any issues are found during the syntax check, they are reported with an appropriate error message.
 */
public class SyntaxValidator {

    /**
     * Validates the syntax of the given Go code. It checks various elements such as package declaration, imports,
     * function definitions, loops, conditionals, variable declarations, and more. It also checks for matching parentheses
     * and curly braces, and ensures that semicolons are not used (which are not allowed in Go).
     *
     * @param code The Go code to validate
     * @throws SyntaxException if any syntax errors are found
     */
    public static void checkSyntax(String code) throws SyntaxException {
        Stack<Character> stack = new Stack<>();
        ArrayList<String> errors = new ArrayList<>();
        boolean inCondition = false;
        boolean inAssignment = false;
        boolean inVarDeclaration = false;
        boolean inFuncCall = false;

        String[] lines = code.split("\n");

        boolean hasPackage = false;
        boolean hasImport = false;

//        String packageName = "service";  // Expected package name for validation

        boolean isInForLoop = false;
        boolean isInIfStatement = false;
        boolean isInFunction = false;

        // Loop through each line of the provided code
        for (String line : lines) {
            if (line.startsWith("//")) continue;
            String trimmedLine = line.trim();

            // Skip empty lines
            if (trimmedLine.isEmpty()) {
                continue;
            }

            // Check for non-English alphabet characters
            if (!trimmedLine.matches("[\\x00-\\x7F]*")) {
                errors.add("Error: Non-English characters detected in the code.");
            }

            // Check for semicolons (which are not allowed outside of for loops)
            if (trimmedLine.contains(";")) {
                if (isInForLoop) {
                    // Semicolon inside for loop is allowed (as part of the loop structure)
                    if (!trimmedLine.contains("for")) {
                        errors.add("Error: Invalid semicolon in for loop statement.");
                    }
                } else {
                    errors.add("Error: Semicolons are not allowed outside of for loop.");
                }
            }

            // Check for the package declaration
            if (trimmedLine.startsWith("package")) {
                hasPackage = true;
                // Validate the package name
//                if (!trimmedLine.contains(packageName)) {
//                    errors.add("Error: Expected package name 'service', found: " + trimmedLine);
//                }
            }

            // Check for the import statement (case-insensitive check for "fmt")
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
                checkForParentheses(trimmedLine);  // Check for matching parentheses in the condition
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
                checkForParentheses(trimmedLine);  // Check for matching parentheses in the loop
                isInForLoop = true;
                if (trimmedLine.contains("{")) {
                    stack.push('{');
                }
            }

            // Handle curly braces (either directly after "for", "if", or "func")
            else if (trimmedLine.equals("{") || trimmedLine.equals("}")) {
                if (trimmedLine.equals("{")) {
                    stack.push('{');
                } else if (trimmedLine.equals("}")) {
                    if (stack.isEmpty()) {
                        errors.add("Error: Mismatched closing curly brace.");
                    } else {
                        stack.pop();
                    }
                }
            }

            // Handle assignment checks for variable declarations
            else if (trimmedLine.contains("=") && !trimmedLine.startsWith("var")) {
                checkAssignmentSyntax(trimmedLine, errors);  // Validate assignment syntax
            }

            // Handle variable declaration (using the "var" keyword)
            else if (trimmedLine.toLowerCase().startsWith("var")) {
                inVarDeclaration = true;
                checkVarDeclaration(trimmedLine, errors);  // Validate variable declaration syntax
            }

            // Handle function calls
            else if (trimmedLine.contains("(") && trimmedLine.contains(")")) {
                inFuncCall = true;
                checkFunctionCall(trimmedLine, errors);  // Validate function call syntax
            }

            // Handle unknown syntax or incorrect keywords
            else {
                errors.add("Error: Unknown syntax or incorrect keyword: " + trimmedLine);
            }

            // Reset state after processing a line
            inAssignment = false;
            inVarDeclaration = false;
            inFuncCall = false;

            // If we exit the for loop, reset the for loop flag
            if (trimmedLine.contains("}")) {
                isInForLoop = false;
            }
        }

        // Check for unmatched curly braces
        if (!stack.isEmpty()) {
            errors.add("Error: Unmatched opening curly brace.");
        }

        // Ensure that package and import declarations are present
        if (!hasPackage) {
            errors.add("Error: Missing 'package' declaration.");
        }
        if (!hasImport) {
            errors.add("Error: Missing 'import' declaration.");
        }

        // Output errors or success message
        if (!errors.isEmpty()) {
            throw new SyntaxException(errors);
        }
    }

    /**
     * Checks if parentheses in a line are properly matched.
     *
     * @param line The line of code to check for parentheses balance.
     * @throws SyntaxException if mismatched parentheses are found.
     */
    private static void checkForParentheses(String line) throws SyntaxException {
        int openParentheses = 0;
        int closeParentheses = 0;

        for (char c : line.toCharArray()) {
            if (c == '(') openParentheses++;
            if (c == ')') closeParentheses++;
        }

        if (openParentheses != closeParentheses) {
            throw new SyntaxException("Error: Mismatched parentheses in condition.");
        }
    }

    /**
     * Validates the syntax of an assignment operation.
     *
     * @param line The line of code to check for valid assignment syntax.
     * @param errors The list of errors to accumulate if any issues are found.
     * @throws SyntaxException if invalid assignment syntax is found.
     */
    private static void checkAssignmentSyntax(String line, ArrayList<String> errors) throws SyntaxException {
        // Skip assignment checks for variable declarations like 'var x int = 5'
        if (line.startsWith("var") && line.contains("=")) {
            return;  // Skip this check
        }

        // Check for assignment inside fmt.Println()
        if (line.contains("fmt.Println") && line.contains("=")) {
            if (line.contains("==")) {
                return;  // This is a comparison, not an assignment
            }
            errors.add("Error: Invalid assignment inside fmt.Println() function call.");
            return;
        }

        // Regular assignment handling (e.g., a = 5)
        String[] parts = line.split("=");
        if (parts.length != 2 || parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) {
            errors.add("Error: Invalid assignment syntax.");
        }
    }

    /**
     * Validates the syntax of a variable declaration.
     *
     * @param line The line of code to check for valid variable declaration syntax.
     * @param errors The list of errors to accumulate if any issues are found.
     * @throws SyntaxException if invalid variable declaration syntax is found.
     */
    private static void checkVarDeclaration(String line, ArrayList<String> errors) throws SyntaxException {
        String trimmedLine = line.trim();

        // Check for incorrect capitalization of 'var'
        if (trimmedLine.matches(".*\\b(VAr|VAR)\\b.*")) {
            throw new SyntaxException("Error: 'Var' is not valid. Use 'var' for variable declaration.");
        }

        if (!trimmedLine.startsWith("var")) {
            throw new SyntaxException("Error: Invalid variable declaration.");
        }

        String[] parts = trimmedLine.split("\\s+");

        if (parts.length < 3) {
            errors.add("Error: Invalid variable declaration (missing type or variable name).");
            return;
        }

        if (!parts[1].matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
            errors.add("Error: Invalid variable name: " + parts[1]);
            return;
        }

        if (!parts[2].matches("int|float64|string|bool")) {
            errors.add("Error: Invalid variable type: " + parts[2]);
            return;
        }

        if (parts.length > 3) {
            String assignmentPart = trimmedLine.substring(trimmedLine.indexOf('=')).trim();

            if (assignmentPart.isEmpty()) {
                errors.add("Error: Invalid assignment in variable declaration (missing value).");
            }
        }
    }

    /**
     * Validates the syntax of a function call.
     *
     * @param line The line of code to check for valid function call syntax.
     * @param errors The list of errors to accumulate if any issues are found.
     * @throws SyntaxException if invalid function call syntax is found.
     */
    private static void checkFunctionCall(String line, ArrayList<String> errors) throws SyntaxException {
        // Check for invalid function call syntax (missing parentheses)
        if (!line.contains("(") || !line.contains(")")) {
            errors.add("Error: Invalid function call syntax.");
            return;
        }

        // Check for fmt.print with a string inside, which should be an error
        if (line.contains("fmt.print") && line.contains("\"") && line.contains(")")) {
            // Ensure that the string is inside the parentheses, which indicates a string literal being passed
            int startIdx = line.indexOf("fmt.print");
            int openParenIdx = line.indexOf("(", startIdx);
            int closeParenIdx = line.indexOf(")", openParenIdx);

            if (openParenIdx != -1 && closeParenIdx != -1) {
                // Extract the content inside the parentheses
                String insideParentheses = line.substring(openParenIdx + 1, closeParenIdx).trim();

                // Check if the content inside parentheses is a string literal (starts and ends with a quote)
                if (insideParentheses.startsWith("\"") && insideParentheses.endsWith("\"")) {
                    errors.add("Error: Unexpected syntax. 'fmt.print' cannot have a string inside. Use 'fmt.Println' instead.");
                }
            }
        }

        // Check for fmt.Print (capital P)
        if (line.contains("fmt.Print")) {
            errors.add("Error: Use of 'fmt.Print' is not allowed. Use 'fmt.Println' instead.");
        }
    }

}