/*
 * Reference
 * https://firebase.googleblog.com/2017/12/using-android-architecture-components.html
 */

package com.example.kitchen.data.firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class FoodViewModel extends ViewModel {
    private static final DatabaseReference FOOD_REF =
            FirebaseDatabase.getInstance().getReference(References.FOOD);

    @NonNull
    public LiveData<DataSnapshot> getDataSnapshotLiveData() {
        return new FirebaseQueryLiveData(FOOD_REF.orderByKey());
    }

    public void addFood(String name, float conversionMultiplier) {
        FOOD_REF.child(name).setValue(conversionMultiplier);
    }
}
