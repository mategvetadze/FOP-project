package service.exeption;

public class SyntaxException extends Throwable {
    SyntaxException(){
        super("Found Syntax Error");
    }
}
