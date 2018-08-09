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

import com.example.kitchen.data.firebase.models.RecipeModel;
import com.example.kitchen.data.local.entities.Recipe;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.HashMap;
import java.util.Map;

public class RecipeViewModel extends ViewModel {
    private static final String TAG = "FirebaseDatabase";
    private static final int NONE = 0;
    private static final int HIGHEST = 5;
    private static final DatabaseReference RECIPE_REF =
            FirebaseDatabase.getInstance().getReference(References.RECIPES);
    private final FirebaseQueryLiveData liveData = new FirebaseQueryLiveData(RECIPE_REF);

    @NonNull
    public LiveData<DataSnapshot> getDataSnapshotLiveData() {
        return liveData;
    }

    public String postRecipe(Recipe recipe, String imageUrl) {
        String key;
        if (TextUtils.isEmpty(recipe.publicKey)) {
            key = RECIPE_REF.push().getKey();
        } else {
            key = recipe.publicKey;
        }
        if (!TextUtils.isEmpty(key)) {
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("title", recipe.title);
            childUpdates.put("imageUrl", imageUrl);
            childUpdates.put("servings", recipe.servings);
            childUpdates.put("prepTime", recipe.prepTime);
            childUpdates.put("cookTime", recipe.cookTime);
            childUpdates.put("language", recipe.language);
            childUpdates.put("cuisine", recipe.cuisine);
            childUpdates.put("course", recipe.course);
            childUpdates.put("writerUid", recipe.writerUid);
            childUpdates.put("writerName", recipe.writerName);
            RECIPE_REF.child(key).updateChildren(childUpdates);
        }
        return key;
    }

    public void postRating(String publicKey, final int rating, final int lastRating, final RatingPostListener listener) {
        RECIPE_REF.child(publicKey).runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                RecipeModel recipe = mutableData.getValue(RecipeModel.class);
                if (recipe == null) {
                    return Transaction.success(mutableData);
                }
                if (lastRating <= HIGHEST && lastRating >= NONE && rating <= HIGHEST && rating >= NONE) {
                    if (lastRating == NONE) {
                        recipe.ratingCount = recipe.ratingCount + 1;
                    }
                    recipe.totalRating = recipe.totalRating + rating - lastRating;
                    // Set value and report transaction success
                    mutableData.setValue(recipe);
                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
                listener.onRatingTransactionSuccessful(rating);
            }
        });
    }

    public interface RatingPostListener {
        void onRatingTransactionSuccessful(int rating);
    }
}
