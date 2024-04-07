/* 
File: FileHandler.java
Authors: Kenny Collins, Anthony Rojas, Scott Schnieders, Rakan Al rasheed
Date: 4/6/2024
Description: This file is responsible for all File Handling Methods.
*/

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class FileHandler {
    String trainPath = "train/";
    String testPath = "test/";
    String weightPath = "weights/";
    String resultPath = "results/";

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

        ArrayList<ArrayList<ArrayList<Integer>>> dataSet = new ArrayList<>();

        for (int i = 0; i < capacity; i++) { // for each image vector
            ArrayList<ArrayList<Integer>> imageVector = new ArrayList<>();

            for (int j = 0; j < numRows; j++) { // for each row in the image vector
                if (!sc.hasNextLine()) break;
                String line = sc.nextLine().trim();
                ArrayList<Integer> row = new ArrayList<>();
                
                // Using binary since hebb net training
                for (char cell : line.toCharArray()) { 
                    if (cell == 'O') {
                        row.add(1);
                    } else if (cell == ' ') {
                        row.add(0);
                    }
                }
                imageVector.add(row);
            }
            dataSet.add(imageVector);
        }

        sc.close();
        return new InputData(numDimensions, capacity, dataSet);
    }

    public void saveWeights(String weights){
        // TODO: Need to implement but super chill method.

    }

    public void loadWeights(String weightSettingsFile){
        // TODO: Call inside test Method
    }

    static class InputData {
        // Stores information for Net to read.
        int numDimensions;
        int capacity;
        ArrayList<ArrayList<ArrayList<Integer>>> dataSet;

        public InputData(int numDimensions, int capacity,
                        ArrayList<ArrayList<ArrayList<Integer>>> dataSet) {
            this.numDimensions = numDimensions;
            this.capacity = capacity;
            this.dataSet = dataSet;
        }
    }
}
