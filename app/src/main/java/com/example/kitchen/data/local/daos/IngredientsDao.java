package com.example.kitchen.data.local.daos;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.kitchen.data.local.entities.Ingredient;

import java.util.List;

@Dao
public interface IngredientsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insertIngredient(Ingredient ingredient);

    @Update
    void update(Ingredient ingredient);

    @Delete
    void deleteIngredient(Ingredient ingredient);

    @Query("SELECT * from ingredients WHERE recipe_id = :recipeId ORDER BY id")
    LiveData<List<Ingredient>> getIngredientsByRecipe(int recipeId);

    @Query("SELECT * from ingredients WHERE food = :food ORDER BY recipe_id")
    LiveData<List<Ingredient>> getIngredientsByFood(String food);

}
