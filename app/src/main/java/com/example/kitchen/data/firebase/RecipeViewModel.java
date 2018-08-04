/*
 * Reference => https://firebase.googleblog.com/2017/12/using-android-architecture-components.html
 */
package com.example.kitchen.data.firebase;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.example.kitchen.data.firebase.models.Recipe;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RecipeViewModel extends ViewModel {
    private static final DatabaseReference RECIPE_REF =
            FirebaseDatabase.getInstance().getReference(References.RECIPES);
    private final FirebaseQueryLiveData liveData = new FirebaseQueryLiveData(RECIPE_REF);

    @NonNull
    public LiveData<DataSnapshot> getDataSnapshotLiveData() {
        return liveData;
    }

    public String writeNewRecipe(String title, String photoUrl, int servings, int prepTime, int cookTime,
                                 String language, String cuisine, String course, String writer) {

        String key = RECIPE_REF.push().getKey();
        Recipe recipe = new Recipe(title, photoUrl, servings, prepTime, cookTime, language, cuisine, course, writer);
        if (key != null)
            RECIPE_REF.child(key).setValue(recipe);

        return key;
    }
}
