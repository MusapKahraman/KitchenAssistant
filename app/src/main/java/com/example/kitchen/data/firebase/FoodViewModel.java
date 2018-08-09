/*
 * Reference
 * https://firebase.googleblog.com/2017/12/using-android-architecture-components.html
 */

package com.example.kitchen.data.firebase;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FoodViewModel extends ViewModel {
    private static final DatabaseReference FOOD_REF =
            FirebaseDatabase.getInstance().getReference(References.FOOD);
    private final FirebaseQueryLiveData liveData = new FirebaseQueryLiveData(FOOD_REF);

    @NonNull
    public LiveData<DataSnapshot> getDataSnapshotLiveData() {
        return liveData;
    }

    public void addFood(String name, float conversionMultiplier) {
        FOOD_REF.child(name).setValue(conversionMultiplier);
    }
}
