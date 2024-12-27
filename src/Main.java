import service.Interpreter;
import service.exeption.SyntaxException;

import java.util.HashMap;

public class Main {
    public static void main(String[] args) throws Exception, SyntaxException {
        String code= """
                //go:build isPalindrome
                
                package main
                
                
                func main() {
                	
                	fmt.Println(5==5)
                }
                
                
                """;
        Interpreter interpreter = new Interpreter(code);
        interpreter.interpret();
    }
}