package com.example.kitchen.data.local.daos;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.example.kitchen.data.local.entities.Recipe;

import java.util.List;

@Dao
public interface RecipesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRecipe(Recipe recipe);

    @Delete
    void deleteRecipe(Recipe recipe);

    @Query("SELECT * from recipes WHERE title = :title")
    LiveData<Recipe> getRecipe(String title);

    @Query("SELECT * from recipes ORDER BY timeStamp")
    LiveData<List<Recipe>> getAll();

}
