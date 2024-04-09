/* 
File: HopfieldNet.java
Authors: Kenny Collins, Anthony Rojas, Scott Schnieders, Rakan Al rasheed
Date: 4/6/2024
Description: Responsible for training and testing the Hopfield. Utilizes FileHandler for modularity.
*/

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class HopfieldNet {
    FileHandler fileHandler = new FileHandler();
    public boolean stoppingCondition;

    public class Hopfield{
        // Class for loading/storing Net
        int numDimensions;
        int capacity;
        int[][][] storedPatterns;
        int[][] weights;

        public Hopfield(int numDimensions, int capacity, 
                        int[][][] storedPatterns,
                        int[][] weights){
                        this.numDimensions = numDimensions;
                        this.capacity = capacity;
                        this.storedPatterns = storedPatterns;
                        this.weights = weights;
                        }
    }
    public void train(String trainingDataFile, String weightSettingsFile) throws FileNotFoundException {
        // Passing file data into Hebb for training and initializing variables
        FileHandler.InputData inputData = fileHandler.readInputData(trainingDataFile, fileHandler.trainPath);
        int numDimensions = inputData.numDimensions;
        int capacity = inputData.capacity;
        int numRows = (int)Math.sqrt(numDimensions);
        int[][][] storedPatterns = inputData.dataSet;
        int [][] weights = new int[numRows][numRows];

        // Hebb Net learning
        for (int[][] pattern : storedPatterns) {
            int[][] transposedMatrix = transposeMatrix(pattern);
            // Wi = S^T * S
            int[][] multipliedMatrix = multiplyMatrices(transposedMatrix, pattern);
        
            for (int i = 0; i < weights.length; i++) {
                for (int j = 0; j < weights[i].length; j++) {
                    // Skip diagonal elements to prevent self-connections
                    if (i != j) {
                        weights[i][j] += multipliedMatrix[i][j];
                    }
                }
            }
        }
        
        Hopfield hopNet = new Hopfield(numDimensions, capacity, storedPatterns, weights);

        // Save Parameters, pattrerns, and weights to file.
        fileHandler.saveWeights(weightSettingsFile, hopNet);
    }
    public void test(String testingDataFile, String resultsFile){
        // TODO: Implement, shouldn't be too bad
    }

    public int[][] transposeMatrix(int[][] matrix) {
        // Once again, vectors might be faster
        int rows = matrix.length;
        int cols = matrix[0].length;
        int[][] transposed = new int[cols][rows];
    
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                // Swap elements
                transposed[j][i] = matrix[i][j];
            }
        }
        return transposed;
    }


    public int[][] multiplyMatrices(int[][] matrixA, int[][] matrixB) {
        // TODO: OPTIMIZE, vectors might be better tbh
        int[][] result = new int[matrixA.length][matrixB[0].length];
    
        for (int i = 0; i < matrixA.length; i++) {
            for (int j = 0; j < matrixB[0].length; j++) {
                for (int k = 0; k < matrixA[0].length; k++) {
                    result[i][j] += matrixA[i][k] * matrixB[k][j];
                }
            }
        }
    
        return result;
    }
}
