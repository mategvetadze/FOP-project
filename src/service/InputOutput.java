package service;

import service.exeption.InvalidOutput;

import java.util.Scanner;

public class InputOutput {
    private  Interpreter interpreter;
    private static final Scanner scanner = new Scanner(System.in);

    public InputOutput(Interpreter interpreter) {
        this.interpreter = interpreter;

    }

    public  void inputHandler(String Line) {
        Line.replaceAll(" ", "");
        int n = Line.length();
        for (int k = 0; k < n; k++) {
            if (Line.charAt(k) == '&') {
                StringBuilder s = new StringBuilder();
                k++;
                while (Line.charAt(k) != ',' || Line.charAt(k) != ')') {
                    s.append(Line.charAt(k));
                    k++;
                }
                interpreter.addVariable(s.toString(), scanner.nextInt());
            }
        }
    }

    public  void outputHandler(String Line) throws InvalidOutput {
        Line.replaceAll(" ", "");
        int n = Line.length();
        int ExceptionCnt=0;
        int ind1=Line.indexOf('('), ind2=Line.lastIndexOf(')');
        String Output=Line.substring(ind1+1,ind2);
        ind1=Output.indexOf('"');
        ind2=Output.lastIndexOf('"');
        if(ind1==ind2 && ind1!=-1){
            throw new InvalidOutput();
        }
        else if(ind1!=-1){
            System.out.print(Output.substring(ind1+1,ind2));
        }
        else{
            ExceptionCnt++;
            try {
                System.out.print(String_ToInt(Output));
            } catch (Exception e) {
                ExceptionCnt++;
                try {
                    System.out.print(String_ToBool(Output));
                }
                catch (Exception b){
                    ExceptionCnt++;
                }
            }
        }
        if(ExceptionCnt==3) throw new InvalidOutput();
        else if(Line.contains("Println")) System.out.println();
    }
    public  Integer String_ToInt(String s){
        return (int) interpreter.getVariableTypes("int").apply(s);
    }
    public  Boolean String_ToBool(String s) {
        return (boolean) interpreter.getVariableTypes("bool").apply(s);
    }
}
