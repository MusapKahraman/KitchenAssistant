/*
 * Reference
 * https://github.com/firebase/FirebaseUI-Android/blob/master/auth/README.md
 */

package com.example.kitchen.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.kitchen.BuildConfig;
import com.example.kitchen.R;
import com.example.kitchen.utility.AppConstants;
import com.example.kitchen.utility.DeviceUtils;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements DeviceUtils.InternetConnectionListener {
    private static final int RC_SIGN_IN = 123;
    private static final String LOG_TAG = LoginActivity.class.getSimpleName();
    private static final String KEY_APP_WIDGET = "app-widget-key";
    @BindView(R.id.tv_connect_internet_try_again) TextView mNoConnectionTextView;
    @BindView(R.id.progress_bar_connection_check) ProgressBar mProgressBar;
    private boolean mIsStartedByAppWidget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        if (savedInstanceState == null) {
            mIsStartedByAppWidget = getIntent()
                    .getBooleanExtra(AppConstants.EXTRA_APP_WIDGET, false);
        } else {
            mIsStartedByAppWidget = savedInstanceState.getBoolean(KEY_APP_WIDGET);
        }
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // already signed in
            Intent intent = new Intent(this, MainActivity.class);
            if (mIsStartedByAppWidget) intent.putExtra(AppConstants.EXTRA_APP_WIDGET, true);
            startActivity(intent);
            finish();
        } else {
            // not signed in
            DeviceUtils.startConnectionTest(this);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_APP_WIDGET, mIsStartedByAppWidget);
    }

    @Override
    public void onConnectionResult(boolean success) {
        mProgressBar.setVisibility(View.GONE);
        if (success) {
            // Set night mode on. This will make the password hiding toggle icon visible for darker
            // backgrounds. But this line restarts the activity. Using this line in onCreate will
            // result in an unresponsive design.
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            List<AuthUI.IdpConfig> configList = Arrays.asList(
                    new AuthUI.IdpConfig.GoogleBuilder().build(),
                    new AuthUI.IdpConfig.EmailBuilder().build());
            Intent authIntent = AuthUI.getInstance().createSignInIntentBuilder()
                    .setAvailableProviders(configList)
                    .setIsSmartLockEnabled(!BuildConfig.DEBUG, true)
                    .setTheme(R.style.LoginTheme)
                    .setLogo(R.mipmap.logo)
                    .build();
            startActivityForResult(authIntent, RC_SIGN_IN);
        } else {
            mNoConnectionTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v(LOG_TAG, "onActivityResult");
        if (requestCode == RC_SIGN_IN) {
            IdpResponse idpResponse = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                Log.v(LOG_TAG, "Successfully signed in");
                Intent intent = new Intent(this, MainActivity.class);
                if (mIsStartedByAppWidget) intent.putExtra(AppConstants.EXTRA_APP_WIDGET, true);
                startActivity(intent);
                finish();
            } else {
                // Sign in failed
                if (idpResponse == null) {
                    // User pressed back button
                    Log.v(LOG_TAG, "Sign_in_cancelled");
                    finish();
                    return;
                }
                if (idpResponse.getError() != null
                        && idpResponse.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Log.e(LOG_TAG, "No network");
                    return;
                }
                Log.e(LOG_TAG, "Sign-in error: ", idpResponse.getError());
            }
        }
    }
}
