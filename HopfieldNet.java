/* 
File: HopfieldNet.java
Authors: Kenny Collins, Anthony Rojas, Scott Schnieders, Rakan Al rasheed
Date: 4/6/2024
Description: Responsible for training and testing the Hopfield. Utilizes FileHandler for modularity.
*/

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class HopfieldNet {

    private class Hopfield{
        // Class for loading/storing Net
        int numDimensions;
        int capacity;
        ArrayList<ArrayList<ArrayList<Integer>>> storedPatterns;
        ArrayList<ArrayList<Integer>> weights;

        public Hopfield(int numDimensions, int capacity, 
                        ArrayList<ArrayList<ArrayList<Integer>>> storedPatterns,
                        ArrayList<ArrayList<Integer>> weights){
                        this.numDimensions = numDimensions;
                        this.capacity = capacity;
                        this.storedPatterns = storedPatterns;
                        this.weights = weights;
                        }
    }
    public void train(String trainingDataFile, String weightSettingsFile) throws FileNotFoundException {
        /*
        * Notes for training:
        * Train using hebb net, setting Negative diagonal to zero.
        * threshold is typically zero for hebb nett, and binary inputs are used.
        * Pretty straightforward.
        */
        Hopfield hopNet = new Hopfield(0, 0, null, null);
    }
    public void test(String testingDataFile, String resultsFile){
        // TODO: Implement, shouldn't be too bad
    }

}
