import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class WeightMatrixTester {

    public static void main(String[] args) {
        // Define the directory and file path to read input data from
        String directory = "";
        String filePath = "sample-data.txt";

        try {
            // Read the input data from the file
            ArrayList<ArrayList<ArrayList<Integer>>> dataSet = readInputData(directory, filePath);

            // Print the input dataset for verification
            System.out.println("Input Dataset:");
            printDataset(dataSet);

            // Generate the weight matrix
            genWeightMatrix(dataSet);
        } catch (FileNotFoundException e) {
            // Handle file not found exception
            System.out.println("File not found: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to read input data from a file
    private static ArrayList<ArrayList<ArrayList<Integer>>> readInputData(String directory, String filePath) throws FileNotFoundException {
        ArrayList<ArrayList<ArrayList<Integer>>> dataSet = new ArrayList<>();
        File file = new File(directory + filePath);
        Scanner scanner = new Scanner(file);

        while (scanner.hasNextLine()) {
            ArrayList<ArrayList<Integer>> imageVector = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                ArrayList<Integer> row = new ArrayList<>();
                String line = scanner.nextLine();
                for (char c : line.toCharArray()) {
                    if (c == 'O') {
                        row.add(1);
                    } else if (c == ' ') {
                        row.add(0);
                    }
                }
                imageVector.add(row);
            }
            dataSet.add(imageVector);
        }
        scanner.close();
        return dataSet;
    }

    // Method to print the dataset for verification
    private static void printDataset(ArrayList<ArrayList<ArrayList<Integer>>> dataSet) {
        for (ArrayList<ArrayList<Integer>> imageVector : dataSet) {
            for (ArrayList<Integer> row : imageVector) {
                for (Integer pixel : row) {
                    System.out.print(pixel + " ");
                }
                System.out.println();
            }
            System.out.println();
        }
    }

    // Method to generate weight matrix
    public static void genWeightMatrix(ArrayList<ArrayList<ArrayList<Integer>>> dataSet) {
        // Transpose the dataset
        ArrayList<ArrayList<ArrayList<Integer>>> dataSetTranspose = transpose(dataSet);

        // Perform matrix multiplication (dataset * transpose)
        ArrayList<ArrayList<Integer>> weightMatrix = matrixMultiplication(dataSet, dataSetTranspose);

        // Display the resulting weight matrix
        System.out.println("Weight Matrix:");
        for (ArrayList<Integer> row : weightMatrix) {
            for (Integer element : row) {
                System.out.print(element + " ");
            }
            System.out.println();
        }
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

    // Method to perform matrix multiplication
    public static ArrayList<ArrayList<Integer>> matrixMultiplication(ArrayList<ArrayList<ArrayList<Integer>>> matrix1,
                                                            ArrayList<ArrayList<ArrayList<Integer>>> matrix2) {
                                                                
        ArrayList<ArrayList<Integer>> result = new ArrayList<>();
        int numRows1 = matrix1.size();
        int numCols1 = matrix1.get(0).get(0).size();
        int numCols2 = matrix2.get(0).get(0).size();

        for (int i = 0; i < numRows1; i++) {
        ArrayList<Integer> newRow = new ArrayList<>();
        for (int j = 0; j < numCols2; j++) {
        int sum = 0;
        for (int k = 0; k < numCols1; k++) {
        sum += matrix1.get(i).get(0).get(k) * matrix2.get(j).get(0).get(k);
        }
        newRow.add(sum);
        }
        result.add(newRow);
        }
        return result;
}
}
