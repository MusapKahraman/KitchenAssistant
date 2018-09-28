package com.example.kitchen.data.local.daos;

import com.example.kitchen.data.local.entities.Step;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface StepsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Step step);

    @Update
    void update(Step step);

    @Delete
    void delete(Step step);

    @Query("SELECT * from steps WHERE recipeId = :recipeId")
    LiveData<List<Step>> getStepsByRecipe(int recipeId);

}
