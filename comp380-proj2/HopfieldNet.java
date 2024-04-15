/* 
File: HopfieldNet.java
Authors: Kenny Collins, Anthony Rojas, Scott Schnieders, Rakan Al rasheed
Date: 4/6/2024
Description: Responsible for training and testing the Hopfield. Utilizes FileHandler for modularity.
*/

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class HopfieldNet {
    private final int THETA = 0;
    public boolean stoppingCondition;

    public static class Hopfield{
        // Class for loading/storing Net
        int numDimensions;
        int capacity;
        int[][][] storedPatterns;
        int[][][] storedPatternsBipolar;
        int[][] weights;

        public Hopfield(int numDimensions, int capacity, int[][][] storedPatterns, int[][] weights) {
                        this.numDimensions = numDimensions;
                        this.capacity = capacity;
                        this.storedPatterns = storedPatterns;
                        this.storedPatternsBipolar = convertToBipolar(storedPatterns);
                        this.weights = weights; }

    }
    public void train(String trainingDataFile, String weightSettingsFile) throws FileNotFoundException {
        // Passing file data into Hebb for training and initializing variables
        FileHandler.InputData inputData;
        try {
            inputData = FileHandler.readInputData(FileHandler.trainPath, trainingDataFile);
        } catch (FileNotFoundException e) {
            System.out.println("Error: File Not Found");
            e.printStackTrace(); // Print the stack trace for debugging
            return;
        }
        int numDimensions = inputData.numDimensions;
        int capacity = inputData.capacity;
        int numRows = (int)Math.sqrt(numDimensions);
        int[][][] storedPatterns = inputData.dataSet;
        int[][][] storedPatternsBipolar = convertToBipolar(storedPatterns);
        int [][] weights = new int[numRows][numRows];

        // Hebb Net learning
        for (int[][] pattern : storedPatternsBipolar) {
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

        // Save Parameters, patterns, and weights to file.
        FileHandler.saveWeights(weightSettingsFile, hopNet);
    }

    
    public void test(String testingDataFile, String resultsFile, Hopfield weightsAndPatterns){
        // Read in data from the input testing file and Initialize values
        FileHandler.InputData inputData;
        try {
            inputData = FileHandler.readInputData(FileHandler.testPath, testingDataFile);
        } catch (FileNotFoundException e) {
            System.out.println("Error: File Not Found");
            e.printStackTrace(); // Print the stack trace for debugging
            return;
        }
        if(inputData.numDimensions != weightsAndPatterns.numDimensions) { // Check the dimensions match
            System.out.println("Test Data dimensions does not match weights");
            return;
        }
        int[][][] storedPatterns = weightsAndPatterns.storedPatterns;
        int[][] weights = weightsAndPatterns.weights;
        int numRows = (int)Math.sqrt(inputData.numDimensions);

        // Application of the Hopfield net
        int[] results = new int[inputData.dataSet.length];
        for (int k = 0; k < inputData.dataSet.length; k++) {
            int[][] pattern = inputData.dataSet[k]; // For every pattern in dataset
            int matchedPattern = -1;
            do {
                int[][] y = deepCopy(pattern);
                for (int i = 0; i < pattern.length; i++) { // For every row in pattern
                    int ySize = numRows;
                    // Choose random unit
                    int[] randomInds = genRandomIndArray(ySize);
                    for(int unitIndex : randomInds) { // For every index in a randomized list
                        // Calculate y_in for the unit
                        int sum = 0;
                        for (int j = 0; j < numRows; j++) {
                            sum += (weights[j][unitIndex] * y[i][j]);
                        }
                        int yin = pattern[i][unitIndex] + sum; // Add x of i and the sum
                        // Activation function
                        if (yin > THETA) {
                            y[i][unitIndex] = 1;
                        } else {
                            y[i][unitIndex] = 0;
                        }
                    }
                }
                pattern = y;
                matchedPattern = checkForConvergence(storedPatterns, y);
            } while (matchedPattern == -1);
            // Found a match, put in the results
            results[k] = matchedPattern;
        }
        Hopfield data = new Hopfield(inputData.numDimensions,inputData.capacity, inputData.dataSet, weights);
        FileHandler.saveResults(resultsFile,weightsAndPatterns,data,results);
    }

    /*
     * Deep copies a 2d arrays
     */
    private static int[][] deepCopy(int[][] array) {
        if (array == null) {
            return null;
        }

        int[][] newArray = new int[array.length][];
        for (int i = 0; i < array.length; i++) {
            // Make a copy of each sub-array
            newArray[i] = new int[array[i].length];
            for (int j = 0; j < array[i].length; j++) {
                newArray[i][j] = array[i][j];
            }
        }
        return newArray;
    }

    // Function to generate an array of random indexes
    public static int[] genRandomIndArray(int size) {
        int[] indexes = new int[size];
        Random random = new Random();

        // Initialize the array with consecutive integers from 0 to size - 1
        for (int i = 0; i < size; i++) {
            indexes[i] = i;
        }

        // Shuffle the array
        for (int i = size - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            // Swap elements
            int temp = indexes[i];
            indexes[i] = indexes[j];
            indexes[j] = temp;
        }

        return indexes;
    }

    /*
     * This function takes in the stored patterns and a pattern to check.
     * It returns -1 if there is no match or the index of the match.
     */
    private int checkForConvergence(int[][][] storedPatterns, int[][] checkPattern) {
        for (int i = 0; i< storedPatterns.length; i++) {
            if(Arrays.deepEquals(storedPatterns[i], checkPattern)) {
                return i;
            }
        }
        return -1;
    }

    private int[][] transposeMatrix(int[][] matrix) {
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


    private int[][] multiplyMatrices(int[][] matrixA, int[][] matrixB) {
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

    // Function to convert binary to bipolar
    public static int[][][] convertToBipolar(int[][][] binaryArrays) {
        int[][][] bipolarArrays = new int[binaryArrays.length][][];
        for (int i = 0; i < binaryArrays.length; i++) {
            int[][] binaryArray = binaryArrays[i];
            int[][] bipolarArray = new int[binaryArray.length][binaryArray[0].length];
            for (int row = 0; row < binaryArray.length; row++) {
                for (int col = 0; col < binaryArray[row].length; col++) {
                    if (binaryArray[row][col] == 0) {
                        bipolarArray[row][col] = -1;
                    } else {
                        bipolarArray[row][col] = 1;
                    }
                }
            }
            bipolarArrays[i] = bipolarArray;
        }
        return bipolarArrays;
    }
}
