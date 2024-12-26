package service;

import model.Value;

import java.util.*;
import java.util.function.Function;

/**
 * {@code Interpreter} class<br>
 * TODO
 *  @author Luka kalandadze
 */
public class Interpreter {

    /**
     * stores commands supported by the interpreter
     */
    private Set<String> commands;

    /**
     * stores variable Types supported by the interpreter and the function, with which we convert the input string to the variable
     */
    private HashMap<String, Function<String, ?>> variableTypes;

    /**
     * stores variable names and its values during the running of the code
     */
    private HashMap<String, Value> variables;

    /**
     * stores the inputted code line by line
     */
    private String[] lines;

    /**
     * {@code Program Counter} pointer of which line is being interpreted currently
     */
    private int pc;

    /**
     * stores where loops and if statements should return after running code in its block
     */
    private Stack<Integer> returnAddresses;


    /**
     * allows to create {@code Interpreter} class object
     *
     * @param input input is the code that will be interpreted line by line
     */
    public Interpreter(String input) {
        variables = new HashMap<>();
        commands = new HashSet<>();
        commands.add("for");
        commands.add("if");

        variableTypes = new HashMap<>();
        variableTypes.put("int", x -> {
            String[] parts = x.split("[+\\-*/%]");
            for (String part : parts) {
                if (!part.matches("\\d+")) {
                    if (variables.containsKey(part)) {
                        x = x.replace(part, variables.get(part).getValue().get().toString());
                    } else {
                        throw new RuntimeException("Variable " + part + " is not defined");
                    }
                }
            }
            try {
                return ArithmeticExpressionCalculator.evaluateExpression(x);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        variableTypes.put("float", Float::parseFloat);
        variableTypes.put("double", Double::parseDouble);
        variableTypes.put("char", x -> x.trim().charAt(1));
        variableTypes.put("boolean", x -> {
            String[] parts = x.split("[&|!]");
            for (String part : parts) {
                if (!part.matches("true|false")) {
                    if (variables.containsKey(part)) {
                        x = x.replace(part, variables.get(part).getValue().get().toString());
                    } else {
                        throw new RuntimeException("Variable " + part + " is not defined");
                    }
                }
            }
            try {
                return BooleanExpressionCalculator.evaluate(x);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        this.lines = input.split(" (\n)|(\\{)");
        pc = 0;
    }

    public void interpret() throws Exception {
        while (pc < lines.length) {
            while (lines[pc].startsWith("//")) pc++;
            interpretLine(lines[pc]);
            //pc++;
        }
    }

    /**
     * Inputs a Line which it later Interprets
     *
     * @param line line which the function will interpret
     * @throws Exception TODO
     */
    private void interpretLine(String line) throws Exception {

        String command = line.strip().split("[ =]")[0];

        if (line.contains("}")) {
            pc = returnAddresses.pop();
        } else if (command.equals("for")) {
            executeFor(line);

        } else if (command.equals("if")) {
            executeIf(line);

        } else if (command.equals("var")) {
            initVariables(line);
        } else if (variables.containsKey(command)) {

        } else {
            executeNonCommand(line);
        }
    }

    private void falseStatement() {
        while (!lines[pc].contains("}")) pc++;
        pc++;
    }

    private void executeIf(String line) throws Exception {
        String condition = line.strip().replace("if", "").replace("{", "");
        if (BooleanExpressionCalculator.evaluate(condition)) {
            pc++;
        } else {
            falseStatement();
        }
    }

    private void alterVariable(String line) {
        String[] words = line.split("=");
        String name = words[0];
        String value = words[1];

        variables.put(name,new Value( Optional.of(variables.get(name).getType().apply(value)),variables.get(name).getType()));

    }

    private void executeFor(String line) throws Exception {
        String condition = line.strip().replace("for", "").replace("{", "");
        if (BooleanExpressionCalculator.evaluate(condition)) {
            returnAddresses.push(pc);
            pc++;
        } else {
            falseStatement();
        }
        //TODO
    }


    /**
     * Executes a line which is neither an Initialization nor a command(for, while, if...)
     *
     * @param line TODO
     * @throws Exception TODO
     */
    private void executeNonCommand(String line) {

    }

    /**
     * If an inputted line is an initialization of a variable initialize it and store it in {@code variables}
     *
     * @param line line which the function will interpret
     * @throws Exception TODO
     */
    private void initVariables(String line) {
        String[] words = line.replace("=", " ").split(" ");
        String name = words[1];
        variables.put(name, new Value(Optional.empty(), variableTypes.get(words[2])));
        if (line.contains("=")) {
            String variable = words[2];
            String value = line.substring(line.indexOf("=") + 1);
            variables.put(name, new Value(Optional.of(variableTypes.get(variable).apply(value)), variableTypes.get(words[2])));
        }
    }
}
