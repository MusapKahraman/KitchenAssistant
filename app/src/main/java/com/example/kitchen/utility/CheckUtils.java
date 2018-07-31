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
}
