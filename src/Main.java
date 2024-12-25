import model.ArithmeticOperation;
import service.ArithmeticOperationCalculator;
import service.Interpreter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Function;

public class Main {
    public static void main(String[] args) {
        String op="5+10*2/4-9/3+7%4+21";
        System.out.println(ArithmeticOperationCalculator.calculate(op,new HashMap<>()));
    }
}