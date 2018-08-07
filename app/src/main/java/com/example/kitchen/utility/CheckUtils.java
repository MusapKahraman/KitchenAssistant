package com.example.kitchen.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CheckUtils {
    /**
     * Checks if the user is online
     *
     * @param context context to get the connectivity system service from
     * @return true if the user is connected or connecting to a network
     */
    public static boolean isOnline(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = null;
        if (manager != null) {
            netInfo = manager.getActiveNetworkInfo();
        }
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static String validateRecipeTitle(String title) {
        // Separate each character of the input title.
        char[] chars = title.toCharArray();
        // Empty outcome string.
        title = "";
        // Reserve a little box for preventing adjacent spaces.
        char previous = '.';
        // For each character...
        for (char aChar : chars) {
            // Allow letters and spaces; prevent adjacent spaces.
            if (Character.isLetter(aChar) || (aChar == ' ' && previous != ' ')) {
                title = title.concat(String.valueOf(aChar));
                previous = aChar;
            }
        }
        // Delete surrounding spaces and make all characters lower case.
        title = title.trim().toLowerCase();
        // Separate each word.
        String[] words = title.split(" ");
        // Empty outcome string.
        title = "";
        // For each word...
        for (int i = 0; i < words.length; i++) {
            // Separate each character.
            chars = words[i].toCharArray();
            // For each character...
            for (int j = 0; j < chars.length; j++) {
                // Make first letters of each word a capital letter.
                // There is not any one-letter word to be a capital letter in the middle of a sentence.
                if ((i == 0 && j == 0) || (i != 0 && j == 0 && chars.length > 1)) {
                    chars[j] = Character.toUpperCase(chars[j]);
                }
            }
            // Combine filtered characters.
            title = title.concat(String.valueOf(chars));
            if (i != words.length - 1) {
                title = title.concat(" ");
            }
        }
        return title;
    }
}
