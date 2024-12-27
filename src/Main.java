import service.Interpreter;
import service.exeption.SyntaxException;

import java.util.HashMap;

public class Main {
    public static void main(String[] args) throws Exception, SyntaxException {
        String code= """
                //go:build isPalindrome
                
                package main
                
                import "fmt"
                
                func main() {
                	var a int
                	fmt.Scan(&a)
                	var reversed int
                	var copyOfA int = a
                	for copyOfA > 0 {
                		reversed = reversed*10 + copyOfA%10
                		copyOfA = copyOfA / 10
                	}
                	fmt.Println(a == reversed)
                }
                
                
                """;
        Interpreter interpreter = new Interpreter(code);
        interpreter.interpret();
    }
}