package com.minecave.errors.loggers;

import com.minecave.errors.ErrorHandling;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Carter on 8/3/2015.
 */
public class FileLogger {

    private static Pattern date = Pattern.compile(
            "((?:(?:[0-1][0-9])|(?:[2][0-3])):(?:[0-5][0-9]):(?:[0-5][0-9]))" + //time
            "(\\s+@+\\s)" + // @
            "((?:(?:[0][0-9])|(?:[1][0-2]))-(?:(?:[0-2][0-9])|([3][0-1]))-(?:[0-9]{4}))"); //date

    public static void logToFile(Throwable thrown, String dateAndTime, String pluginName, boolean shortener)
            throws IOException {

        //Get the path to the folder using the plugin's name
        String folderpath = ErrorHandling.getInstance().getDataFolder() + File.separator + pluginName;

        //Create the folder
        File folder = new File(folderpath);

        //Crate folder if it doesn't exist
        if (!folder.exists())
            folder.mkdir();


        //Create the file to the .log using the date from above
        File file = new File(folderpath
                + File.separator
                + new SimpleDateFormat("dd-MM-yyyy").format(new Date())
                + ".log");

        //Create file if it doesn't exist
        if (!file.exists())
            file.createNewFile();

        String alternateReport = null;
        if(shortener){
            alternateReport = getAlternateReport(file, ExceptionUtils.getStackTrace(thrown));
        }

        FileWriter fileWriter = new FileWriter(file, true);
        PrintWriter printWriter = new PrintWriter(fileWriter);

        printWriter.println("");
        printWriter.println(dateAndTime);
        if(alternateReport == null){
            thrown.printStackTrace(printWriter);
        }else{
            printWriter.println(alternateReport);
        }
        printWriter.close();
        fileWriter.close();
    }

    public static String getAlternateReport(File file, String message) throws IOException {
        //If the file contains the second line of the stack tract.
        String[] stackStrings = message.split("\n", 4);
        for(String string : stackStrings){
            //System.out.println(string);
        }

        if (FileUtils.readFileToString(file).contains(message)) {
            System.out.println(true);
            List<String> fileLines = FileUtils.readLines(file);
            //System.out.println(Pattern.quote(fileLines.get(0)));

            for (int i = 0; i < fileLines.size() - 5; i++) {
                String line1 = fileLines.get(i);
                if (line1.equals("")) {
                    //System.out.println("2");
                    if(!fileLines.get(i+2).contains("Same error as line")){
                        //System.out.println("3");
                        //System.out.println(Pattern.quote(fileLines.get(i+2)));
                        //System.out.println(Pattern.quote(stackStrings[0]));
                        if (stackStrings[0].contains(fileLines.get(i+2))) {
                            //System.out.println("4");
                            boolean equals = true;
                            for (int c = 1; c < 4; c++) {
                                if (!stackStrings[c].contains(fileLines.get(i + 2 + c))) {
                                    equals = false;
                                    break;
                                }
                            }
                            if (equals) {
                                return "Same error as line " + String.valueOf(i+2);
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

}
