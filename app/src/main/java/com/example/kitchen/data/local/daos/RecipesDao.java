/*
 * Reference
 * https://stackoverflow.com/questions/45677230/android-room-persistence-library-upsert/48641762#48641762
 */

package com.example.kitchen.data.local.daos;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.kitchen.data.local.entities.Recipe;

import java.util.List;

@Dao
public interface RecipesDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Recipe recipe);

    @Update
    void update(Recipe recipe);

    @Delete
    void delete(Recipe recipe);

    @Query("SELECT * from recipes WHERE title = :title")
    LiveData<Recipe> getRecipe(String title);

    @Query("SELECT * from recipes WHERE publicKey = :publicKey")
    LiveData<Recipe> getRecipeByPublicKey(String publicKey);

    @Query("SELECT * from recipes ORDER BY timeStamp")
    LiveData<List<Recipe>> getAll();

}
