/* 
File: HopfieldNet.java
Authors: Kenny Collins, Anthony Rojas, Scott Schnieders, Rakan Al rasheed
Date: 4/6/2024
Description: Responsible for training and testing the Hopfield. Utilizes FileHandler for modularity.
*/

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Random;

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
        // Grabbing file data afor training.
        FileHandler.InputData inputData;
        try {
            inputData = FileHandler.readInputData(FileHandler.trainPath, trainingDataFile);
        } catch (FileNotFoundException e) {
            System.out.println("Error: File Not Found");
            e.printStackTrace();
            return;
        }
        int numDimensions = inputData.numDimensions;
        int capacity = inputData.capacity;
        int[][][] storedPatterns = inputData.dataSet;
        int[][][] storedPatternsBipolar = convertToBipolar(storedPatterns);
        int [][] weights = new int[numDimensions][numDimensions];

        // Hebb Net learning
        for (int[][] pattern : storedPatternsBipolar) {
            // flatten Matrix and take outer product to get numDim x numDim weights.
            int [] flatPattern = flattenPattern(pattern);
            int[][] productMatrix = outerProduct(flatPattern);
        
            for (int i = 0; i < weights.length; i++) {
                for (int j = 0; j < weights[i].length; j++) {
                    // skip diagonal for slef connections
                    if (i != j) {
                        weights[i][j] += productMatrix[i][j];
                    }
                }
            }
        }
        
        Hopfield hopNet = new Hopfield(numDimensions, capacity, storedPatterns, weights);
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

        // Convert input patterns to vectors
        int[][] inputVectors = new int[inputData.capacity][inputData.numDimensions];
        for(int i = 0; i<inputData.dataSet.length; i++) {
            inputVectors[i] = toVector(inputData.dataSet[i]);
        }

        // Application of the Hopfield net
        int[] results = new int[inputVectors.length];
        for (int k = 0; k < inputVectors.length; k++) { // For every inputVector in dataset
            int[] pattern = inputVectors[k];
            int matchedPattern = -1;
            do { // try to converge
                int[] y = Arrays.copyOf(pattern, pattern.length);
                // Choose random unit
                int ySize = y.length;
                int[] randomInds = genRandomIndArray(ySize);
                for(int unitIndex : randomInds) { // For every index in a randomized list
                    // Calculate y_in for the unit
                    int sum = 0;
                    for (int j = 0; j < ySize; j++) {
                        sum += (weights[j][unitIndex] * y[j]);
                    }
                    int yin = pattern[unitIndex] + sum; // Add x of i and the sum
                    // Activation function
                    if (yin > THETA) {
                        y[unitIndex] = 1;
                    } else {
                        y[unitIndex] = 0;
                    }
                }
                matchedPattern = checkForConvergence(storedPatterns, y);
                if(matchedPattern == -1 && Arrays.equals(pattern,y)) {
                    matchedPattern = -2;
                } else {
                    pattern = y;
                }
            } while (matchedPattern == -1);
            // Found a match, put in the results
            results[k] = matchedPattern;
        }
        Hopfield data = new Hopfield(inputData.numDimensions,inputData.capacity, inputData.dataSet, weights);
        FileHandler.saveResults(resultsFile,weightsAndPatterns,data,results);
    }

    // Function to convert a 2D integer matrix to a 1D integer array
    public static int[] toVector(int[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        int[] vector = new int[rows * cols];
        int index = 0;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                vector[index++] = matrix[i][j];
            }
        }

        return vector;
    }

    public int[] flattenPattern(int[][] pattern) {
        // Used to flatten pattern to a vector.
        int numRows = pattern.length;
        int numCols = pattern[0].length;
        int[] flatPattern = new int[numRows * numCols];
        
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                flatPattern[i * numCols + j] = pattern[i][j];
            }
        }
        return flatPattern;
    }

    private int[][] outerProduct(int[] vector) {
        // Just need outer product of matrices rather than transposing.
        int length = vector.length;
        int[][] product = new int[length][length];
    
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                product[i][j] = vector[i] * vector[j];
            }
        }
    
        return product;
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
    private int checkForConvergence(int[][][] storedPatterns, int[] checkVector) {
        for (int i = 0; i< storedPatterns.length; i++) {
            if(Arrays.equals(toVector(storedPatterns[i]), checkVector)) {
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
