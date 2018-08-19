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

import com.example.kitchen.data.local.entities.Ingredient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class IngredientViewModel extends ViewModel {
    private static final String LOG_TAG = IngredientViewModel.class.getSimpleName();
    private static final DatabaseReference INGREDIENT_REF =
            FirebaseDatabase.getInstance().getReference(References.INGREDIENTS);

    @NonNull
    public LiveData<DataSnapshot> getDataSnapshotLiveData(String recipeKey) {
        return new FirebaseQueryLiveData(INGREDIENT_REF.orderByChild("recipeKey").equalTo(recipeKey));
    }

    public String postIngredient(Ingredient ingredient, String recipeKey) {
        String key;
        if (TextUtils.isEmpty(ingredient.publicKey)) {
            key = INGREDIENT_REF.push().getKey();
        } else {
            key = ingredient.publicKey;
        }
        if (!TextUtils.isEmpty(key)) {
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("recipeKey", recipeKey);
            childUpdates.put("name", ingredient.food);
            childUpdates.put("amount", ingredient.amount);
            childUpdates.put("amountType", ingredient.amountType);
            INGREDIENT_REF.child(key).updateChildren(childUpdates);
        }
        return key;
    }

    public void removeIngredient(String key) {
        if (!TextUtils.isEmpty(key)) {
            INGREDIENT_REF.child(key).removeValue();
            Log.v(LOG_TAG, "Removed ingredient from firebase database. Key : " + key);
        } else {
            Log.e(LOG_TAG, "No key is given to remove data from firebase database.");
        }
    }
}
