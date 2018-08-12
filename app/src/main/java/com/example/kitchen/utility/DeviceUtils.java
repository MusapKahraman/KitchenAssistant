/*
 * Reference
 * https://stackoverflow.com/questions/1109022/close-hide-the-android-soft-keyboard/17789187#17789187
 */

package com.example.kitchen.utility;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class DeviceUtils {
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

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm != null)
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}