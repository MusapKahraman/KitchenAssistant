package com.example.kitchen.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.kitchen.BuildConfig;
import com.example.kitchen.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;
    private static final String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // already signed in
            // This is an existing user.
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            // not signed in
            startActivityForResult(
                    // Get an instance of AuthUI based on the default app
                    AuthUI.getInstance().createSignInIntentBuilder()
                            .setAvailableProviders(Arrays.asList(
                                    new AuthUI.IdpConfig.GoogleBuilder().build(),
                                    new AuthUI.IdpConfig.EmailBuilder().build()))
                            .setIsSmartLockEnabled(!BuildConfig.DEBUG /* credentials */, true /* hints */)
                            .setTheme(R.style.LoginTheme)
                            .setLogo(R.mipmap.logo)
                            .build(), RC_SIGN_IN);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            IdpResponse idpResponse = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    if (idpResponse != null) {
                        Intent intent;
                        if (idpResponse.isNewUser()) {
                            // The user is new, show them a fancy intro screen!
                            intent = new Intent(this, WelcomeActivity.class);
                        } else {
                            // This is an existing user.
                            intent = new Intent(this, MainActivity.class);
                        }
                        startActivity(intent);
                        finish();
                    }
                }
            } else {
                // Sign in failed
                if (idpResponse == null) {
                    // User pressed back button
                    Log.v(TAG, "Sign_in_cancelled");
                    finish();
                    return;
                }
                if (idpResponse.getError() != null && idpResponse.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Log.v(TAG, "No network");
                    return;
                }

                Log.e(TAG, "Sign-in error: ", idpResponse.getError());
            }
        }
    }
}
