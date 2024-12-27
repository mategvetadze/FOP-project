package service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class FileReader {
    private static final String ROOT="goCode/";
    public static String readFile(String address) throws FileNotFoundException {
        StringBuilder builder = new StringBuilder();

        File myObj = new File(ROOT+address);
        Scanner myReader = new Scanner(myObj);
        while (myReader.hasNextLine()) {
            String data = myReader.nextLine();
            builder.append(data).append("\n");
        }
        myReader.close();

        return builder.toString();
    }

}