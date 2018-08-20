/*
 * Reference
 * https://stackoverflow.com/questions/6493517/detect-if-android-device-has-internet-connection/6493572#6493572
 * https://stackoverflow.com/questions/1109022/close-hide-the-android-soft-keyboard/17789187#17789187
 */

package com.example.kitchen.utility;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class DeviceUtils {
    private static final String LOG_TAG = DeviceUtils.class.getSimpleName();

    private static boolean isNetworkAvailable(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = null;
        if (manager != null) {
            netInfo = manager.getActiveNetworkInfo();
        }
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static void startConnectionTest(InternetConnectionListener listener) {
        Context context = (Context) listener;
        if (isNetworkAvailable(context)) {
            new ConnectionTask(listener).execute();
        } else {
            listener.onConnectionResult(false);
        }
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null)
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public interface InternetConnectionListener {
        void onConnectionResult(boolean success);
    }

    private static class ConnectionTask extends AsyncTask<Void, Void, Boolean> {
        private final InternetConnectionListener listener;

        ConnectionTask(InternetConnectionListener listener) {
            this.listener = listener;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            Log.v(LOG_TAG, "Checking for internet connection...");
            try {
                HttpURLConnection connection = (HttpURLConnection)
                        (new URL("http://clients3.google.com/generate_204")
                                .openConnection());
                connection.setRequestProperty("User-Agent", "Android");
                connection.setRequestProperty("Connection", "close");
                connection.setConnectTimeout(1500);
                connection.connect();
                return (connection.getResponseCode() == 204 &&
                        connection.getContentLength() == 0);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error checking internet connection", e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            listener.onConnectionResult(result);
        }
    }
}