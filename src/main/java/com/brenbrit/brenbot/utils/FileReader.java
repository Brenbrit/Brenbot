package com.brenbrit.brenbot.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileReader {


    // Read a CSV file into an ArrayList<String>, skipping one header line.
    public static ArrayList<String> readCSV(String fileLoc) {

        Logger logger = LoggerFactory.getLogger(FileReader.class);

        logger.debug("Opening csv file at " + fileLoc);

        // csv is read into this ArrayList
        ArrayList<String> lines = new ArrayList<String>();

        try {
            Scanner scanner = new Scanner(new File(fileLoc));
            // skip header line
            scanner.nextLine();

            // read all lines
            while (scanner.hasNextLine())
                lines.add(scanner.nextLine());
            scanner.close();

        } catch (Exception e) {
            logger.error("Error encountered while reading csv file.");
            logger.error(e.getStackTrace().toString());
        }

        return lines;
    }
}
