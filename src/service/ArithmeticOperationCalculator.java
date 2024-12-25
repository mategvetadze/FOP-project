package service;

import model.ArithmeticUnit;

import java.util.HashMap;

public class ArithmeticOperationCalculator {
    public static int calculate(String operation, HashMap<Character,Integer> variables) {
        operation=operation.replaceAll(" ", "");
        ArithmeticUnit unit=new ArithmeticUnit(operation);
        return unit.getValue();
    }
}
