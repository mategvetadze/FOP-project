package service.exeption;

public class InvalidOutput extends RuntimeException {
    public InvalidOutput() {
        super("Invalid Output");
    }
}
