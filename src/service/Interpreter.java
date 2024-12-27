package service;

import model.Value;
import service.exception.*;

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
    private HashSet<String> imports;
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
    private Stack<String> returnInstruction;
    private Stack<Boolean> ifStack;
    private InputOutput inputOutput;

    public Optional<?> getVariable(String name){
        return variables.get(name).getValue();
    }

    /**
     * allows to create {@code Interpreter} class object
     *
     * @param input input is the code that will be interpreted line by line
     */
    public Interpreter(String input) {
        variables = new HashMap<>();
        returnAddresses = new Stack<>();
        ifStack = new Stack<>();
        returnInstruction = new Stack<>();
        imports = new HashSet<>();
        inputOutput =new InputOutput(this);
//        input=input.replaceAll("}\\s*else","}\nelse");
        this.lines = input.split("(\n)|(\\{)");
        pc = 0;

        initCommands();
        initVariableTypes();
    }
    public void addVariable(String name,int value){
        if (!variables.containsKey(name))throw new RuntimeException("Variable " + name + " not found");
        Value value1=variables.get(name);
        value1.setValue(value);
        variables.put(name,value1);
    }

    public void interpret() throws Exception, SyntaxException {
        while (pc < lines.length) {
            while (lines[pc].startsWith("//")) pc++;
            interpretLine(lines[pc]);
        }
    }


    private void initCommands() {
        commands = new HashSet<>();
        commands.add("for");
        commands.add("if");
    }
    public Function<String, ?> getVariableTypes(String s){
        return variableTypes.get(s);
    }
    private void initVariableTypes() {
        variableTypes = new HashMap<>();
        variableTypes.put("int", x -> {
            String[] parts = x.replaceAll("[() ]","").split("[+\\-*/%]");
            for (String part : parts) {
                if (!part.matches("\\d+")) {
                    if (variables.containsKey(part)) {
                        Optional<?> value = variables.get(part).getValue();
                        if (value.isPresent()) {
                            x = x.replaceFirst(part, value.get().toString());
                        } else {
                            x = x.replaceFirst(part, "0");
                        }
                    } else {
                        throw new RuntimeException("Variable " + part + " is not defined");
                    }
                }
            }
            try {
                if (x.matches("true|false")) return x;
                return ArithmeticExpressionCalculator.evaluateExpression(x);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        variableTypes.put("float", Float::parseFloat);
        variableTypes.put("double", Double::parseDouble);
        variableTypes.put("char", x -> x.trim().charAt(1));
        variableTypes.put("bool", x -> {
            String[] parts = x.replaceAll(" ", "").replace("!", "").split("&&|\\|\\||==|>=|<=|>|<");
            for (String part : parts) {
                if (!part.matches("true|false")) {
                    String a =  variableTypes.get("int").apply(part)+"";
                    x = x.replaceFirst(part, a);
                }
            }
            try {
                return BooleanExpressionCalculator.evaluate(x);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

    }

    /**
     * Inputs a Line which it later Interprets
     *
     * @param line line which the function will interpret
     * @throws Exception TODO
     */
    private void interpretLine(String line) throws Exception, SyntaxException {

        String command = line.strip().split("[. =]")[0];
        if (line.contains("else")) {
            executeElse(line);
        } else if (command.equals("for")) {
            executeLoop(line);
        } else if (command.equals("if")) {
            executeIf(line);
        }else if (command.equals("var")) {
            initVariables(line);
        } else if (imports.contains(command)) {
            handleImportFunction(line);
        } else if (variables.containsKey(command)||line.contains("--")||line.contains("++")||line.contains("%=")||line.contains("*=")||line.contains("/=")||line.contains("+=")||line.contains("-=")) {
            alterVariable(line);
        }else if (command.equals("import")||command.equals("package")||command.equals("func")) {
            executeNonCommand(line);
        } else if (line.contains("}")) {
            int a;
            if (returnAddresses.isEmpty()) {
                pc = lines.length;
            } else if ((a = returnAddresses.pop()) != lines.length + 1) {
                pc = a - 1;
                if (!returnInstruction.isEmpty()) {
                    String returnLine = returnInstruction.pop();
                    interpretLine(returnLine);
                    pc--;
                }
            }
        } else if (!line.isEmpty()){
            throw new RuntimeException("Unknown command on: " + line);
        }
        pc++;
    }

    private void handleImportFunction(String line) throws SyntaxException {
        String[] words = line.strip().split("\\.");
        String command = words[0];
        if (Objects.equals(command, "fmt")){
            if (words[1].contains("Print")) inputOutput.outputHandler(words[1]);
            else if (words[1].contains("Scan")) inputOutput.inputHandler(words[1]);
        }
    }


    private void falseStatement() {
        Stack<Integer> stack = new Stack<>();
        pc++;
        while (pc < lines.length && (!lines[pc].contains("}") || !stack.isEmpty())) {
            if (lines[pc].contains("for") || (lines[pc].contains("if") && !lines[pc].contains("else"))) stack.push(1);
            if (lines[pc].contains("}")) stack.pop();
            if (!stack.isEmpty() && lines[pc].contains("else")) stack.push(1);
            pc++;
        }
        if ((lines[pc].contains("else") || lines[pc].contains("for") || lines[pc].contains("if"))) pc--;
    }

    private void executeIf(String line) throws Exception {
        String condition = line.strip().replace("if", "").replace("{", "");
        boolean bool = (boolean) variableTypes.get("bool").apply(condition);
        ifStack.push(bool);
        if (!bool) {
            falseStatement();
        } else returnAddresses.push(lines.length + 1);
    }

    private void executeElse(String line) throws Exception {
        if (ifStack.isEmpty()) throw new Exception("Else with on if statement");
        boolean bool = ifStack.peek();
        if (line.replace("}","").startsWith("if")) {
            String condition = line.strip().replace("if", "").replace("else", "").replace("{", "");
            if (!(boolean) variableTypes.get("bool").apply(condition) || bool) {
                falseStatement();
            } else {
                returnAddresses.push(lines.length + 1);
                ifStack.pop();
                ifStack.push(true);
            }
        } else {
            if (bool) {
                if (line.contains("}")) ifStack.pop();
                falseStatement();
            } else returnAddresses.push(lines.length + 1);
        }
    }

    private void alterVariable(String line) {
        if (alterVariableShortcut(line)) return;
        line = line.replaceAll(" ", "").trim();
        String name = line.substring(0, line.indexOf('='));
        String value = line.strip().substring(line.indexOf('=') + 1);
        variables.put(name, new Value(Optional.of(variables.get(name).getType().apply(value)), variables.get(name).getType()));
    }


    private boolean alterVariableShortcut(String line) {
        if (line.contains("++")){
            String name = line.replaceAll(" ", "").replaceAll("\\+","");
            alterVariable(name+"="+name+"+1");
            return true;
        }else if (line.contains("--")){
            String name = line.replaceAll(" ", "").replaceAll("-","");
            alterVariable(name+"="+name+"+1");
            return true;
        }else if (line.contains("-=")){
            String[] parts = line.trim().split("-=");
            alterVariable(parts[0]+"="+parts[0]+"-"+parts[1]);
            return true;
        }else if (line.contains("+=")){
            String[] parts = line.trim().split("\\+=");
            alterVariable(parts[0]+"="+parts[0]+"+"+parts[1]);
            return true;
        }else if (line.contains("*=")){
            String[] parts = line.trim().split("\\*=");
            alterVariable(parts[0]+"="+parts[0]+"*("+parts[1]+")");
            return true;
        }else if (line.contains("/=")){
            String[] parts = line.trim().split("/=");
            alterVariable(parts[0]+"="+parts[0]+"/("+parts[1]+")");
            return true;
        }else if (line.contains("%=")){
            String[] parts = line.trim().split("%=");
            alterVariable(parts[0]+"="+parts[0]+"%("+parts[1]+")");
            return true;
        }
        return false;
    }


    private void executeLoop(String line) throws Exception {
        String condition = line.strip().replace("for", "").replace("{", "");
        String[] parts = condition.split(";");
        if (parts.length == 1) executeWhile(condition);
        else if (parts.length == 3) executeFor(parts);
    }

    private void executeFor(String[] conditions) {
        if (!variables.containsKey(conditions[0].split(":=")[0].trim())) initVariableNoReference(conditions[0]);
        if ((boolean) variableTypes.get("bool").apply(conditions[1])) {
            returnAddresses.push(pc);
            returnInstruction.push(conditions[2]);
        } else {
            falseStatement();
        }
    }

    private void initVariableNoReference(String condition) {
        String[] parts = condition.replaceAll(" ", "").split(":=");
        String variableName = parts[0];
        String value = parts[1];
        try {
            int val = ArithmeticExpressionCalculator.evaluateExpression(value);
            variables.put(variableName, new Value(Optional.of(val), variableTypes.get("int")));
        } catch (Exception e) {
            boolean val = Boolean.parseBoolean(condition);
            variables.put(variableName, new Value(Optional.of(val), variableTypes.get("bool")));
        }
    }

    private void executeWhile(String condition) throws Exception {
        if ((boolean) variableTypes.get("bool").apply(condition)) {
            returnAddresses.push(pc);
        } else {
            falseStatement();
        }
    }


    /**
     * Executes a line which is neither an Initialization nor a command(for, while, if...)
     *
     * @param line TODO
     * @throws Exception TODO
     */
    private void executeNonCommand(String line) {
        if (line.strip().startsWith("package")) {
            //
        } else if (line.strip().startsWith("import")) {
            imports.add(line.replace("import ", "").replaceAll("[\" ]",""));
        } else if (line.strip().startsWith("func")) {
            //
        }
    }

    /**
     * If an inputted line is an initialization of a variable initialize it and store it in {@code variables}
     *
     * @param line line which the function will interpret
     * @throws Exception TODO
     */
    private void initVariables(String line) {
        String[] words = line.replaceFirst("=", " ").strip().split(" ");
        String name = words[1];
        variables.put(name, new Value(Optional.empty(), variableTypes.get(words[2])));
        if (line.contains("=")) {
            String variable = words[2];
            String value = line.substring(line.indexOf("=") + 1);
            variables.put(name, new Value(Optional.of(variableTypes.get(variable).apply(value)), variableTypes.get(words[2])));
        }
    }
}


