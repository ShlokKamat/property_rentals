package com.example.rentalapp;

public class Utils {

    static float[] means = new float[]{970.90830822f,2.19974171f,4.11493758f,1.7572105f};
    static float[] stds = new float[]{689.26193329f,3.68153938f,4.34937374f,0.89890801f};

    public static float[] scaleInputForModel(float[] inputArray) {
        float[] scaledInput = new float[inputArray.length];
        for (int i = 0; i < inputArray.length; i++) {
            if(i<4){
                scaledInput[i] = (inputArray[i] - means[i]) / stds[i];
            } else {
                scaledInput[i] = inputArray[i];
            }
        }
        return scaledInput;
    }
}