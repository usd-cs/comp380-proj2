/* 
File: HopfieldNet.java
Authors: Kenny Collins, Anthony Rojas, Scott Schnieders, Rakan Al rasheed
Date: 4/2/2024
Description: Responsible for training and testing the Hopfield. Utilizes FileHandler for modularity.
*/

import java.io.FileNotFoundException;

public class HopfieldNet {
    public void train(String trainingDataFile, String weightSettingsFile) throws FileNotFoundException {
        /*
        * Notes for training:
        * Train using hebb net, setting Negative diagonal to zero.
        * threshold is typically zero for hebb nett, and binary inputs are used.
        * Pretty straightforward.
        */
    }
    public void test(String testingDataFile, String resultsFile){
        // TODO: Implement, shouldn't be too bad
    }
    public void loadWeights(String weightSettingsFile){
        // TODO: Call inside test Method
    }

}
