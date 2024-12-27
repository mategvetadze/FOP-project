import service.FileReader;
import service.Interpreter;
import service.exception.SyntaxException;

import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception, SyntaxException {
        Scanner scanner = new Scanner(System.in);
        String input;
        System.out.println("""
                Place go file in the goCode package and write the file name in the terminal to run the file
                
                Enter \"exit\" to quit.
                
                =========================================================================================
                 
                """);
        System.out.print("enter file name: ");
        while (!(input = scanner.next()).equalsIgnoreCase("exit")) {
            try {
                String code=FileReader.readFile(input);
                Interpreter interpreter = new Interpreter(code);
                interpreter.interpret();
                System.out.print("\n\nenter file name: ");
            }catch (FileNotFoundException e){
                System.out.print("\nFile not found!\nEnter valid file name: ");
            }
        }

    }
}