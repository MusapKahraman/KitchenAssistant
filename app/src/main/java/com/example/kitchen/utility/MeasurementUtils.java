package com.example.kitchen.utility;

import android.content.Context;

import com.example.kitchen.R;

public class MeasurementUtils {

    public static float getConversionMultiplier(Context context, int volume, int volumeType, int weight, int weightType) {
        float volumeInMilliliters = volume * getVolume(context, volumeType);
        float weightInMilligrams = weight * getWeight(context, weightType);
        return weightInMilligrams / volumeInMilliliters;
    }

    private static int getVolume(Context context, int volumeType) {
        switch (volumeType) {
            case 0:
                return context.getResources().getInteger(R.integer.milliliters);
            case 1:
                return context.getResources().getInteger(R.integer.teaspoons);
            case 2:
                return context.getResources().getInteger(R.integer.tablespoons);
            case 3:
                return context.getResources().getInteger(R.integer.cups);
            case 4:
                return context.getResources().getInteger(R.integer.pints);
            case 5:
                return context.getResources().getInteger(R.integer.quarts);
            case 6:
                return context.getResources().getInteger(R.integer.liters);
            case 7:
                return context.getResources().getInteger(R.integer.gallons);
            default:
                return 0;
        }
    }

    private static int getWeight(Context context, int volumeType) {
        switch (volumeType) {
            case 0:
                return context.getResources().getInteger(R.integer.grams);
            case 1:
                return context.getResources().getInteger(R.integer.ounces);
            case 2:
                return context.getResources().getInteger(R.integer.pounds);
            case 3:
                return context.getResources().getInteger(R.integer.kilograms);
            default:
                return 0;
        }
    }
}
