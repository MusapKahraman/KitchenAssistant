package com.example.kitchen.utility;

import android.content.Context;

import com.example.kitchen.R;

public class MeasurementUtils {

    public static float getConversionMultiplier(Context context, int volume, int volumeType, int weight, int weightType) {
        float volumeInMilliliters = volume * getVolume(context, volumeType);
        float weightInMilligrams = weight * getWeight(context, weightType);
        return weightInMilligrams / volumeInMilliliters;
    }

    private static int getVolume(Context context, int weightType) {
        switch (weightType) {
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

    public static String getAbbreviation(Context context, String measurement) {
        if (measurement.equals(context.getResources().getString(R.string.pounds))) {
            return context.getResources().getString(R.string.pounds_abbr);
        } else if (measurement.equals(context.getResources().getString(R.string.ounces))) {
            return context.getResources().getString(R.string.ounces_abbr);
        } else if (measurement.equals(context.getResources().getString(R.string.milligrams))) {
            return context.getResources().getString(R.string.milligrams_abbr);
        } else if (measurement.equals(context.getResources().getString(R.string.grams))) {
            return context.getResources().getString(R.string.grams_abbr);
        } else if (measurement.equals(context.getResources().getString(R.string.kilograms))) {
            return context.getResources().getString(R.string.kilograms_abbr);
        } else if (measurement.equals(context.getResources().getString(R.string.teaspoons))) {
            return context.getResources().getString(R.string.teaspoons_abbr);
        } else if (measurement.equals(context.getResources().getString(R.string.tablespoons))) {
            return context.getResources().getString(R.string.tablespoons_abbr);
        } else if (measurement.equals(context.getResources().getString(R.string.cups))) {
            return context.getResources().getString(R.string.cups_abbr);
        } else if (measurement.equals(context.getResources().getString(R.string.quarts))) {
            return context.getResources().getString(R.string.quarts_abbr);
        } else if (measurement.equals(context.getResources().getString(R.string.milliliters))) {
            return context.getResources().getString(R.string.milliliters_abbr);
        } else if (measurement.equals(context.getResources().getString(R.string.liters))) {
            return context.getResources().getString(R.string.liters_abbr);
        } else if (measurement.equals(context.getResources().getString(R.string.pints))) {
            return context.getResources().getString(R.string.pints_abbr);
        } else if (measurement.equals(context.getResources().getString(R.string.gallons))) {
            return context.getResources().getString(R.string.gallons_abbr);
        } else {
            return context.getResources().getString(R.string.pieces_abbr);
        }
    }
}
