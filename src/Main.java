import service.Interpreter;

import java.util.Arrays;
import java.util.function.Function;

public class Main {
    public static void main(String[] args) {
        String input= "int a = 0; sout(5); if (a=8){sout(a);a=7;}sout(a);";
        String[] lines= input.split("(;)|(\\{)");
        System.out.println(Arrays.toString(lines));
    }
}