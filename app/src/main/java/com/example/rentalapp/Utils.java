package com.example.rentalapp;

import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Utils {

    static float[] means = new float[]{970.90830822f, 2.19974171f, 4.11493758f, 1.7572105f};
    static float[] stds = new float[]{689.26193329f, 3.68153938f, 4.34937374f, 0.89890801f};

    public static float[] scaleInputForModel(float[] inputArray) {
        float[] scaledInput = new float[inputArray.length];
        for (int i = 0; i < inputArray.length; i++) {
            if (i < 4) {
                scaledInput[i] = (inputArray[i] - means[i]) / stds[i];
            } else {
                scaledInput[i] = inputArray[i];
            }
        }
        return scaledInput;
    }

    public static boolean isUserLoggedIn() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        return auth.getCurrentUser() != null;
    }

    public static void logoutCurrentUser() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signOut();
    }

    public static String getUserName() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        return Objects.requireNonNull(auth.getCurrentUser()).getDisplayName();
    }

    public static String getUserEmail() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        return Objects.requireNonNull(auth.getCurrentUser()).getEmail();
    }


    public static float[] setInputArrayFromProperty(PropertyDataClass propertyData) {

        Map<String, Integer> bhk_type_map = new HashMap<>();
        bhk_type_map.put("1 RK",0);
        bhk_type_map.put("1 BHK",1);
        bhk_type_map.put("2 BHK",2);
        bhk_type_map.put("3 BHK",3);
        bhk_type_map.put("4 BHK",4);

        Map<String, Integer> property_age_map = new HashMap<>();
        property_age_map.put("0 – 1 years",0);
        property_age_map.put("1 – 3 years",1);
        property_age_map.put("3 – 5 years",2);
        property_age_map.put("5 – 10 years",3);
        property_age_map.put("> 10 years",4);

        Map<String, Integer> furnishing_map = new HashMap<>();
        furnishing_map.put("Unfurnished",0);
        furnishing_map.put("Semi Furnished",1);
        furnishing_map.put("Fully Furnished",2);

        Map<String, Integer> parking_map = new HashMap<>();
        parking_map.put("No",0);
        parking_map.put("Bike",1);
        parking_map.put("Car",2);
        parking_map.put("Bike & Car",3);

        Map<String, Integer> security_map = new HashMap<>();
        security_map.put("No",0);
        security_map.put("Yes",1);

        float[] inputArray = new float[]{
                (float) propertyData.getPropertySize(), //Size
                (float) propertyData.getFloor(), //Floor
                (float) propertyData.getTotalFloors(), //Total Floor
                (float) propertyData.getNumberOfBathrooms(), //Bathrooms
                (float) bhk_type_map.get(propertyData.getBhkType()), //BHK
                (float) property_age_map.get(propertyData.getPropertyAge()), //Age
                (float) furnishing_map.get(propertyData.getFurnishingType()), //Furnishing
                (float) parking_map.get(propertyData.getParking()), //Parking
                (float) security_map.get(propertyData.getSecurity())  //Security
        };

        return inputArray;
    }
}