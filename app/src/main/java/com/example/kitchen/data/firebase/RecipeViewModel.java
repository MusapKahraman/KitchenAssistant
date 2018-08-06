/*
 * References
 * https://firebase.googleblog.com/2017/12/using-android-architecture-components.html
 * https://firebase.google.com/docs/database/android/read-and-write#save_data_as_transactions
 */
package com.example.kitchen.data.firebase;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.kitchen.data.firebase.models.RecipeModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

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

    public String writeNewRecipe(String title, String photoUrl, int servings, int prepTime, int cookTime,
                                 String language, String cuisine, String course, String writerUid, String writerName) {

        String key = RECIPE_REF.push().getKey();
        RecipeModel recipe = new RecipeModel(title, photoUrl, servings, prepTime, cookTime, language, cuisine, course, writerUid, writerName);
        if (key != null)
            RECIPE_REF.child(key).setValue(recipe);

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
