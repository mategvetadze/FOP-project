package service;

import java.util.*;
import java.util.function.Function;

/**
 * {@code Interpreter} class<br>
 * TODO
 *  @author  Luka kalandadze
 */
public class Interpreter {
    /**
     * stores commands supported by the interpreter
     */
    private Set<String> commands;

    /**
     * stores variable Types supported by the interpreter and the function, with which we convert the input string to the variable
     */
    private HashMap<String,Function<String,?>> variableTypes;

    /**
     * stores variable names and its values during the running of the code
     */
    private HashMap<String, Optional<?>> variables;

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
        variableTypes.put("int", Integer::parseInt);
        variableTypes.put("float",Float::parseFloat);
        variableTypes.put("double",Double::parseDouble);
        variableTypes.put("char",x->x.charAt(1));
        variableTypes.put("boolean",x->x.equals("true"));

        this.lines= input.split(" (\n)|(\\{)");
        pc = 0;
    }

    public void interpret() {
        while (pc < lines.length) {
            while (lines[pc].startsWith("//")) pc++;
            interpretLine(lines[pc]);
            pc++;
        }
    }

    /**
     * Inputs a Line which it later Interprets
     *
     * @param line line which the function will interpret
     * @throws Exception TODO
     */
    private void interpretLine(String line) {

        String command = line.split(" ")[0];

        if (line.contains("}")){
            pc=returnAddresses.pop();
        } else if (command.equals("for")) {

        } else if (command.equals("if")) {

        } else if (command.equals("var")) {
            initVariables(line);
        } else{
            executeNonCommand(line);
        }
    }

    private void falseStatement(){
        while (!lines[pc].contains("}")) pc++;
        pc++;
    }

    private void executeFor(String lines){

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
        String[] words = line.replace("="," ").split(" ");
        String name=words[1];
        variables.put(name,Optional.empty());
        if (line.contains("=")) {
            String variable = words[2];
            String value = line.substring(line.indexOf("=")+1);
            variables.put(name,Optional.of(variableTypes.get(variable).apply(value)));
        }
    }
}
