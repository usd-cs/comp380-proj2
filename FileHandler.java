/* 
File: FileHandler.java
Authors: Kenny Collins, Anthony Rojas, Scott Schnieders, Rakan Al rasheed
Date: 4/6/2024
Description: This file is responsible for all File Handling Methods.
*/

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
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
    
        int[][][] dataSet = new int[capacity][][];

        sc.nextLine(); // skip the empty line
        for (int i = 0; i < capacity; i++) { // for each image vector

            ArrayList<int[]> imageVector = new ArrayList<>();

            // Read each row until there's no more input
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if(line.equals("")) break; // skip the empty line
                char[] cleanedArr = line.toCharArray();
                int[] row = new int[cleanedArr.length];
                // Using binary since Hebb net training
                int k = 0; // Index for each cell in the row
                for (char cell : cleanedArr) {
                    if (cell == 'O') {
                        row[k++] = 1;
                    } else {
                        row[k++] = 0;
                    }
                }
                imageVector.add(row);
            }
            dataSet[i] = imageVector.toArray(new int[imageVector.size()][]);
        }
    
        sc.close();
        return new InputData(numDimensions, capacity, dataSet);
    }
    
    public static void saveWeights(String weightSettingsFile, HopfieldNet.Hopfield hopNet) throws FileNotFoundException, UnsupportedEncodingException {
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
    }

        


    public static HopfieldNet.Hopfield loadWeights(String weightSettingsFile) throws FileNotFoundException {
        // set the stored patterns and weights and return the object
        File file = new File(weightPath + weightSettingsFile);
        Scanner scanner = new Scanner(file);
    
        int numDimensions = Integer.parseInt(scanner.nextLine().split(": ")[1]);
        int capacity = Integer.parseInt(scanner.nextLine().split(": ")[1]);

        int[][][] storedPatterns = new int[capacity][][];

        for (int i = 0; i < capacity; i++) { // for each image vector
            scanner.nextLine(); // skip the title
            ArrayList<int[]> imageVector = new ArrayList<>();

            // Read each row until there's no more input
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if(line.equals("")) break; // skip the empty line
                String[] lineArr = line.split(" ");
                int[] row = new int[lineArr.length];
                for (int k = 0; k < lineArr.length; k++) {
                    row[k] = Integer.parseInt(lineArr[k]);
                }
                imageVector.add(row);
            }
            storedPatterns[i] = imageVector.toArray(new int[imageVector.size()][]);
        }
    
        scanner.nextLine(); // Skip to the weight matrix header
        int[][] weights = new int[numDimensions][numDimensions];
        for (int i = 0; i < numDimensions; i++) {
            String[] line = scanner.nextLine().trim().split(" ");
            for (int j = 0; j < numDimensions; j++) {
                weights[i][j] = Integer.parseInt(line[j]);
            }
        }
    
        HopfieldNet.Hopfield netFromFile = new HopfieldNet.Hopfield(numDimensions, capacity, storedPatterns, weights);
        scanner.close();
        return netFromFile;
    }
        


   
    public static void saveResults(String resultsFileName, HopfieldNet.Hopfield net, HopfieldNet.Hopfield data, int[] results) {
        // The results array is structured: index = index of data pattern, value = stored pattern it matches to
        if (net == null || data == null || results == null) {
            System.err.println("Null data cannot be processed.");
            return;
        }
    
        File resultsFile = new File(resultPath + resultsFileName);
        if (!resultsFile.getParentFile().exists()) {
            resultsFile.getParentFile().mkdirs(); // Ensure the directory exists
        }
    
        try (PrintWriter writer = new PrintWriter(resultsFile, "UTF-8")) {
            writer.println("Test Results:");
    
            for (int i = 0; i < results.length; i++) {
                if (i >= data.storedPatterns.length) {
                    writer.println("No more test data available for result index: " + (i + 1));
                    break;
                }
    
                writer.println("Input testing image:");
                printMatrix(writer, data.storedPatterns[i]); // Assuming storedPatterns in 'data' is used for input patterns
    
                writer.println("The associated stored image:");
                if (results[i] < net.storedPatterns.length && results[i] >= 0) {
                    printMatrix(writer, net.storedPatterns[results[i]]); // Print the associated stored pattern
                } else {
                    writer.println("No matching stored pattern found.");
                }
    
                writer.println(); // Add an extra line for separation between entries
            }
        } catch (Exception e) {
            System.err.println("Error writing to results file: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void printMatrix(PrintWriter writer, int[][] matrix) {
        for (int[] row : matrix) {
            for (int cell : row) {
                writer.print((cell == 1 ? "O" : " ")); // Print O for 1 and space for 0
            }
            writer.println();
        }
    }
        
        

}
