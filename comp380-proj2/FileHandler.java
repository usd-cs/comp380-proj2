/* 
File: FileHandler.java
Authors: Kenny Collins, Anthony Rojas, Scott Schnieders, Rakan Al rasheed
Date: 4/6/2024
Description: This file is responsible for all File Handling Methods.
*/

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;
import java.io.PrintWriter;

public final class FileHandler {
    public static final String trainPath = "train/";
    public static final String testPath = "test/";
    public static final String weightPath = "weights/";
    public static final String resultPath = "results/";
    
    static class InputData {
        // Stores information for Net to read.
        int numDimensions;
        int capacity;
        int[][][] dataSet;

        public InputData(int numDimensions, int capacity,
                        int[][][] dataSet) {
            this.numDimensions = numDimensions;
            this.capacity = capacity;
            this.dataSet = dataSet;
        }
    }

    public static InputData readInputData(String directory, String filePath) throws FileNotFoundException {
        /* Can use this method for training and test data
         * Went for modularity this time around.
         */
        File trainDataPath = new File(directory + filePath);
        Scanner sc = new Scanner(trainDataPath);
    
        int numDimensions = sc.nextInt();
        sc.nextLine(); // skip the text in parentheses after the int
        int capacity = sc.nextInt();
        sc.nextLine(); // skip the text in parentheses after the int
        int numRows = (int)Math.sqrt(numDimensions);
    
        int[][][] dataSet = new int[capacity][numRows][numRows];
    
        for (int i = 0; i < capacity; i++) { // for each image vector
            sc.nextLine(); // skip the empty line
            int[][] imageVector = new int[numRows][numRows];
    
            for (int j = 0; j < numRows; j++) { // for each row in the image vector
                if (!sc.hasNextLine()) break;
                String line = sc.nextLine();
                int[] row = new int[numRows];
                char[] cleanedArr = Arrays.copyOfRange(line.toCharArray(), 0, 10);
                
                // Using binary since Hebb net training
                int k = 0; // Index for each cell in the row
                for (char cell : cleanedArr) {
                    if (cell == 'O') {
                        row[k++] = 1;
                    } else if (cell == ' ') {
                        row[k++] = 0;
                    }
                }
                imageVector[j] = row;
            }
            dataSet[i] = imageVector;
        }
    
        sc.close();
        return new InputData(numDimensions, capacity, dataSet);
    }
    
    public static void saveWeights(String weightSettingsFile, HopfieldNet.Hopfield hopNet) {
        try {
            String path = weightPath + weightSettingsFile;
            PrintWriter writer = new PrintWriter(path, "UTF-8");
    
            // Write basic properties
            writer.println("Dimension size: " + hopNet.numDimensions);
            writer.println("Number of Stored Patterns: " + hopNet.capacity);
    
            // Writing the 3D matrix of 2D patterns
            for (int i = 0; i < hopNet.storedPatterns.length; i++) {
                writer.println("Pattern " + (i + 1) + ":");
                for (int j = 0; j < hopNet.storedPatterns[i].length; j++) {
                    for (int k = 0; k < hopNet.storedPatterns[i][j].length; k++) {
                        writer.print(hopNet.storedPatterns[i][j][k] + " ");
                    }
                    writer.println(); // Newline after each row
                }
                writer.println();
            }
    
            // Writing the 2D Weight matrix
            writer.println("Weight Matrix:");
            for (int i = 0; i < hopNet.weights.length; i++) {
                for (int j = 0; j < hopNet.weights[i].length; j++) {
                    writer.print(hopNet.weights[i][j] + " ");
                }
                writer.println(); // Newline after each row
            }
    
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

        
    public static HopfieldNet.Hopfield loadWeights(String weightSettingsFile){
        // TODO: Open the file and read the saved stuff into the variables
        // set the stored patterns and weights and return the object
        int numRows = 5;
        int capacity = 5;
        int[][][] storedPatterns = new int[capacity][numRows][numRows];
        int [][] weights = new int[numRows][numRows];
        HopfieldNet.Hopfield netFromFile = new HopfieldNet.Hopfield(storedPatterns, weights);
        return netFromFile;
    }

    public static void saveResults(String resultsFileName, HopfieldNet.Hopfield net, HopfieldNet.Hopfield data,
                                   int[] results) {
        // The results array is structured: index = index of data pattern, value = stored pattern it matches to
        // TODO: Save the results from the testing to a file (probably needs another param)
    }

}
