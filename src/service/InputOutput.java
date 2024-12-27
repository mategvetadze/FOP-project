package service;

import java.util.Scanner;

public class InputOutput {
    private static Interpreter interpreter;
    private static final Scanner scanner = new Scanner(System.in);

    public InputOutput(Interpreter interpreter) {
        this.interpreter = interpreter;

    }

    public static void inputHandler(String Line) {
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

    public static void outputHandler(String Line) throws InvalidOutput {
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
                System.out.println(Output);
                System.out.print(String_ToInt(Output));
            } catch (Exception e) {
                ExceptionCnt++;
            }
            try {
                System.out.print(String_ToBool(Output));
            }
            catch (Exception e){
                ExceptionCnt++;
            }
        }
        if(ExceptionCnt==3) throw new InvalidOutput();
        else if(Line.contains("Println")) System.out.println();
    }
    public static Integer String_ToInt(String s){
        System.out.println(s);
        return (int) interpreter.getVariableTypes("int").apply(s);
    }
    public static Boolean String_ToBool(String s){
        return (boolean) interpreter.getVariableTypes("bool").apply(s);
    }
    public static void main(String[] args) {
        outputHandler("Print(1+2+3)");
    }
}
