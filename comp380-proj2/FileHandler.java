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
    /*Tony: Not sure how to build this with current input.
     * I will try to build a weight matrix generator with
     * the output of the filehandler directly
     */
    public void saveWeights(String weights){
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

    public void genWeightMatrix(ArrayList<ArrayList<ArrayList<Integer>>> dataSet) {
        // Transpose the dataset
        ArrayList<ArrayList<ArrayList<Integer>>> dataSetTranspose = transpose(dataSet);

        // Perform matrix multiplication (dataset * transpose)
        // ArrayList<ArrayList<Integer>> weightMatrix = matrixMultiplication(dataSet, dataSetTranspose);

    }

    // Method to transpose a 3D matrix
    public static ArrayList<ArrayList<ArrayList<Integer>>> transpose(ArrayList<ArrayList<ArrayList<Integer>>> matrix) {
        ArrayList<ArrayList<ArrayList<Integer>>> result = new ArrayList<>();
        int numRows = matrix.get(0).size();
        int numCols = matrix.get(0).get(0).size();

        for (int i = 0; i < numRows; i++) {
            ArrayList<ArrayList<Integer>> newRow = new ArrayList<>();
            for (int j = 0; j < numCols; j++) {
                ArrayList<Integer> newColumn = new ArrayList<>();
                for (int k = 0; k < matrix.size(); k++) {
                    newColumn.add(matrix.get(k).get(i).get(j));
                }
                newRow.add(newColumn);
            }
            result.add(newRow);
        }
        return result;
    }

    public static ArrayList<ArrayList<ArrayList<Integer>>> matrixMultiplication(ArrayList<ArrayList<ArrayList<Integer>>> matrix1,
                                                                             ArrayList<ArrayList<ArrayList<Integer>>> matrix2) {

    int rows1 = matrix1.size();
    int cols1Depth2 = matrix1.get(0).size(); // Number of columns in the "depth" dimension of matrix1
    int rows2Depth2 = matrix2.size();        // Number of layers in matrix2
    int cols2 = matrix2.get(0).get(0).size();

    // Check for compatible dimensions for multiplication
    if (cols1Depth2 != rows2Depth2) {
        throw new IllegalArgumentException("Matrices cannot be multiplied: Incompatible dimensions");
    }

    // Create the result matrix with appropriate dimensions
    ArrayList<ArrayList<ArrayList<Integer>>> result = new ArrayList<>(rows1);
    for (int i = 0; i < rows1; i++) {
        ArrayList<ArrayList<Integer>> row = new ArrayList<>();
        for (int j = 0; j < 1; j++) {
            ArrayList<Integer> newRow = new ArrayList<>();
            for (int k = 0; k < cols2; k++) {
                int sum = 0;
                for (int l = 0; l < rows2Depth2; l++) {
                    sum += matrix1.get(i).get(0).get(l) * matrix2.get(l).get(0).get(k);
                }
                newRow.add(sum);
            }
            row.add(newRow);
        }
        result.add(row);
    }

    return result; // Return the resulting 3D matrix
}
}
