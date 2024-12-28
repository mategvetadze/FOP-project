package service.exception;

import java.util.List;

public class SyntaxException extends Throwable {
    public SyntaxException(String message){
        super(message);
    }
    public SyntaxException(List<String> messages){
        super(messages.getFirst());
    }
}
