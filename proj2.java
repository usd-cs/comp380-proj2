/* 
File: proj2.java
Authors: Kenny Collins, Anthony Rojas, Scott Schnieders, Rakan Al rasheed
Date: 4/2/2024
Description: This is the driver file of our program.
*/
import java.io.IOException;
import java.util.Scanner;

public class proj2{
    public static void main(String[] args) throws IOException {
        Scanner kb = new Scanner(System.in);
        boolean run = true;

        System.out.println("Welcome to our Second Project: Hopfield Nets:");

        while(run){
            System.out.println("Enter 1 to train or 2 to test your Hopfield Net:");
            int choice = kb.nextInt();

            switch(choice){

                case 1: // Train
                    System.out.println("Enter the training data file name (add .txt extension):");
                    kb.nextLine();
                    String trainingDataFile = kb.nextLine();

                    System.out.println("Enter the file to save your weights:");
                    kb.nextLine();
                    String weightSettingsFile = kb.nextLine();

                    HopfieldNet hopfieldTrain = new HopfieldNet();
                    hopfieldTrain.train(trainingDataFile, weightSettingsFile);

                    System.out.println("Do you want to run the program again?(Y/N)");
                    kb.nextLine();
                    String rerunProgram = kb.nextLine();
                    if (!rerunProgram.equalsIgnoreCase("Y")) {
                        run = false;
                    }
                    break;

                case 2: // Test
                    HopfieldNet hopfieldTest = new HopfieldNet();

                    System.out.println("Enter the testing data file name:");
                    // kb.nextLine(); // Consume newline left-over
                    String testingDataFile = kb.nextLine();            
                
                    System.out.println("Enter a file name to save the testing/deploying results:");
                    String resultsFile = kb.nextLine();
                    
                    System.out.println("Do you want to run the program again?(Y/N)");
                    String rerun = kb.nextLine();
                    if (!rerun.equalsIgnoreCase("Y")) {
                        run = false;
                    }
                    break;

                default:
                    System.out.println("Invalid input. Please enter 1 or 2.");
                    break;

            }
        }
    }
}