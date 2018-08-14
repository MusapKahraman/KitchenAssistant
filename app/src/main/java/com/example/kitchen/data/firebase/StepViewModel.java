/*
 * References
 * https://firebase.googleblog.com/2017/12/using-android-architecture-components.html
 * https://firebase.google.com/docs/database/android/read-and-write#save_data_as_transactions
 */

package com.example.kitchen.data.firebase;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.example.kitchen.data.local.entities.Step;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class StepViewModel extends ViewModel {
    private static final String LOG_TAG = StepViewModel.class.getSimpleName();
    private static final DatabaseReference STEP_REF =
            FirebaseDatabase.getInstance().getReference(References.STEPS);

    @NonNull
    public LiveData<DataSnapshot> getDataSnapshotLiveData(String recipeKey) {
        return new FirebaseQueryLiveData(STEP_REF.orderByChild("recipeKey").equalTo(recipeKey));
    }

    public String postStep(Step step, String recipeKey) {
        String key;
        if (TextUtils.isEmpty(step.publicKey)) {
            key = STEP_REF.push().getKey();
        } else {
            key = step.publicKey;
        }
        if (!TextUtils.isEmpty(key)) {
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("recipeKey", recipeKey);
            childUpdates.put("instruction", step.instruction);
            childUpdates.put("stepNumber", step.stepNumber);
            STEP_REF.child(key).updateChildren(childUpdates);
        }
        return key;
    }

    public void removeStep(String key) {
        if (!TextUtils.isEmpty(key)) {
            STEP_REF.child(key).removeValue();
            Log.v(LOG_TAG, "Removed step from firebase database. Key : " + key);
        } else {
            Log.e(LOG_TAG, "No key is given to remove data from firebase database.");
        }
    }
}
