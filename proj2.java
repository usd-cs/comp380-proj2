/* 
File: proj2.java
Authors: Kenny Collins, Anthony Rojas, Scott Schnieders, Rakan Al rasheed
Date: 4/6/2024
Description: This is the driver file of our program.
*/
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class proj2{
    public static void main(String[] args) throws IOException {
        Scanner kb = new Scanner(System.in);
        boolean run = true;

        System.out.println("Welcome to our Second Project: Hopfield Nets:");

        while(run){
            System.out.println("Enter 1 to train or 2 to test your Hopfield Net:");
            int choice = kb.nextInt();
            kb.nextLine();

            try {
                runProj2(choice, kb);
            } catch (InputMismatchException e) {
                System.out.println("Invalid input");
            } catch (FileNotFoundException e) {
                System.out.println("File not found, try again");
            } catch (IndexOutOfBoundsException e) {
                if(e.getMessage().equals("Index 8 out of bounds for length 8")) {
                    System.out.println("Patterns are not rectangular make sure the row size is consistent");
                } else {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                System.out.println("Something real bad happened: ");
                e.printStackTrace();
            }
            System.out.println("Do you want to run the program again?(Y/N)");
            String rerun = kb.nextLine();
            if (!rerun.equalsIgnoreCase("Y")) {
                run = false;
            }
        }
        kb.close();
    }

    public static void runProj2(int choice, Scanner kb) throws FileNotFoundException, IndexOutOfBoundsException, UnsupportedEncodingException {
        switch(choice){
            case 1: // Train
                System.out.println("Enter the training data file name (add .txt extension):");
                String trainingDataFile = kb.nextLine();
                System.out.println("Training data file name entered: " + trainingDataFile);

                System.out.println("Enter the file to save your weights:");
                String weightSettingsFile = kb.nextLine();

                HopfieldNet hopfieldTrain = new HopfieldNet();
                System.out.println("Weight settings file name entered: " + weightSettingsFile);
                hopfieldTrain.train(trainingDataFile, weightSettingsFile);
                break;

            case 2: // Test
                HopfieldNet hopfieldTest = new HopfieldNet();

                System.out.println("Enter the trained weight settings data file name:");
                String weightSettingsFileTest = kb.nextLine();
                HopfieldNet.Hopfield weightsAndPatterns = FileHandler.loadWeights(weightSettingsFileTest);

                System.out.println("Enter the testing data file name:");
                String testingDataFile = kb.nextLine();

                System.out.println("Enter a file name to save the testing/deploying results:");
                String resultsFile = kb.nextLine();

                hopfieldTest.test(testingDataFile, resultsFile, weightsAndPatterns);
                break;
            default:
                System.out.println("Invalid input. Please enter 1 or 2.");
                break;

        }
    }
}