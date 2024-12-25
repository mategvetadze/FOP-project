package model;

import java.util.HashMap;
import java.util.function.BiFunction;

public class ArithmeticOperation {
    private static final HashMap<String, BiFunction<ArithmeticUnit, ArithmeticUnit, Integer>> operations;
    static  {
        operations = new HashMap<>();
        operations.put("+", (x, y) -> x.getValue() + y.getValue());
        operations.put("-", (x, y) -> x.getValue() - y.getValue());
        operations.put("*", (x, y) -> x.getValue() * y.getValue());
        operations.put("%", (x, y) -> x.getValue() % y.getValue());
        operations.put("/", (x, y) -> x.getValue() / y.getValue());
    }
    public static BiFunction<ArithmeticUnit, ArithmeticUnit,Integer> getOperation(String operation) {
        BiFunction<ArithmeticUnit,ArithmeticUnit,Integer> function=operations.get(operation);
        if(function==null) throw new ArithmeticException("Unknown operation: "+operation);
        return function;
    }
    public static boolean hasPriority(String operation) {
        if (operation.equals("/") || operation.equals("*") || operation.equals("%")) {
            return true;
        } return false;
    }

}
