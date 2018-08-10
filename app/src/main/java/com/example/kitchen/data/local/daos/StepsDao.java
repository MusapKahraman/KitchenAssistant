package com.example.kitchen.data.local.daos;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.example.kitchen.data.local.entities.Step;

import java.util.List;

@Dao
public interface StepsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertStep(Step step);

    @Delete
    void deleteStep(Step step);

    @Query("SELECT * from steps WHERE recipeId = :recipeId")
    LiveData<List<Step>> getStepsByRecipe(int recipeId);

}
