package com.example.kitchen.utility;

import android.content.Context;
import android.text.TextUtils;
import android.widget.EditText;

import com.example.kitchen.R;

public class CheckUtils {

    public static String validateTitle(String title) {
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

    public static boolean isEmptyTextField(Context context, EditText editText) {
        DeviceUtils.hideKeyboardFrom(context, editText);
        if (TextUtils.isEmpty(editText.getText())) {
            editText.setError(context.getString(R.string.field_required));
            editText.requestFocus();
            return true;
        }
        return false;
    }

    public static int getNonZeroPositiveIntegerFromField(Context context, EditText editText) {
        DeviceUtils.hideKeyboardFrom(context, editText);
        if (TextUtils.isEmpty(editText.getText())) {
            editText.setError(context.getString(R.string.field_required));
            editText.requestFocus();
            return -1;
        } else {
            int value = Integer.valueOf(editText.getText().toString());
            if (value < 1) {
                editText.setError(context.getString(R.string.must_positive_integer));
                editText.requestFocus();
                return -1;
            }
            return value;
        }
    }

    public static int getPositiveIntegerFromField(Context context, EditText editText) {
        DeviceUtils.hideKeyboardFrom(context, editText);
        if (TextUtils.isEmpty(editText.getText())) {
            editText.setError(context.getString(R.string.field_required));
            editText.requestFocus();
            return -1;
        } else {
            int value = Integer.valueOf(editText.getText().toString());
            if (value < 0) {
                editText.setError(context.getString(R.string.must_be_integer));
                editText.requestFocus();
                return -1;
            }
            return value;
        }
    }
}
