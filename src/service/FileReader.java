package service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class FileReader {

    // Root directory where the files are located
    private static final String ROOT = "goCode/";

    // Method to read the contents of a file and return it as a string
    public static String readFile(String address) throws FileNotFoundException {
        // StringBuilder to store the file contents
        StringBuilder builder = new StringBuilder();

        // Create a File object with the specified address, prepended with the ROOT directory
        File myObj = new File(ROOT + address);

        // Create a Scanner object to read the file
        Scanner myReader = new Scanner(myObj);

        // Read each line of the file and append it to the StringBuilder
        while (myReader.hasNextLine()) {
            String data = myReader.nextLine();
            builder.append(data).append("\n"); // Add a newline character after each line
        }

        // Close the scanner after reading the file
        myReader.close();

        // Return the contents of the file as a string
        return builder.toString();
    }
}
