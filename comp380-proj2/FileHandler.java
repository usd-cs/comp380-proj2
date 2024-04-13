/* 
File: FileHandler.java
Authors: Kenny Collins, Anthony Rojas, Scott Schnieders, Rakan Al rasheed
Date: 4/6/2024
Description: This file is responsible for all File Handling Methods.
*/

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.PrintWriter;

public class FileHandler {
    String trainPath = "train/";
    String testPath = "test/";
    String weightPath = "weights/";
    String resultPath = "results/";
    
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

    public InputData readInputData(String directory, String filePath) throws FileNotFoundException {
        /* Can use this method for training and test data
         * Went for modularity this time around.
         */
        File trainDataPath = new File(directory + filePath);
        Scanner sc = new Scanner(trainDataPath);
    
        int numDimensions = sc.nextInt();
        int capacity = sc.nextInt();
        int numRows = (int)Math.sqrt(numDimensions);
        sc.nextLine(); // Empty Line after dimensions/capacity
    
        int[][][] dataSet = new int[capacity][numRows][numRows];
    
        for (int i = 0; i < capacity; i++) { // for each image vector
            int[][] imageVector = new int[numRows][numRows];
    
            for (int j = 0; j < numRows; j++) { // for each row in the image vector
                if (!sc.hasNextLine()) break;
                String line = sc.nextLine().trim();
                int[] row = new int[numRows];
                
                // Using binary since Hebb net training
                int k = 0; // Index for each cell in the row
                for (char cell : line.toCharArray()) {
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
    
    public void saveWeights(String weightSettingsFile, HopfieldNet.Hopfield hopNet) {
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

        
    public void loadWeights(String weightSettingsFile){
        // TODO: Call inside test Method
    }

}
