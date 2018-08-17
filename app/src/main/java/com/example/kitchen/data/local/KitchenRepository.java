/*
 * Reference
 * https://stackoverflow.com/questions/45677230/android-room-persistence-library-upsert/48641762#48641762
 */
package com.example.kitchen.data.local;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import com.example.kitchen.data.local.async.DeleteFoodTask;
import com.example.kitchen.data.local.async.DeleteIngredientTask;
import com.example.kitchen.data.local.async.DeleteRecipeTask;
import com.example.kitchen.data.local.async.DeleteStepTask;
import com.example.kitchen.data.local.async.InsertFoodTask;
import com.example.kitchen.data.local.async.InsertIngredientTask;
import com.example.kitchen.data.local.async.InsertRecipeTask;
import com.example.kitchen.data.local.async.InsertStepTask;
import com.example.kitchen.data.local.daos.IngredientsDao;
import com.example.kitchen.data.local.daos.RecipesDao;
import com.example.kitchen.data.local.daos.StepsDao;
import com.example.kitchen.data.local.daos.StorageDao;
import com.example.kitchen.data.local.entities.Food;
import com.example.kitchen.data.local.entities.Ingredient;
import com.example.kitchen.data.local.entities.Recipe;
import com.example.kitchen.data.local.entities.Step;

import java.util.List;

class KitchenRepository {
    // Define data access objects
    private final RecipesDao mRecipesDao;
    private final IngredientsDao mIngredientsDao;
    private final StepsDao mStepsDao;
    private final StorageDao mStorageDao;

    KitchenRepository(Application application) {
        KitchenDatabase db = KitchenDatabase.getDatabase(application);
        mRecipesDao = db.recipesDao();
        mIngredientsDao = db.ingredientsDao();
        mStepsDao = db.stepsDao();
        mStorageDao = db.storageDao();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.

    LiveData<List<Food>> getStorage() {
        return mStorageDao.getStorage();
    }

    LiveData<List<Ingredient>> getIngredientsByRecipe(int recipeId) {
        return mIngredientsDao.getIngredientsByRecipe(recipeId);
    }

    LiveData<Recipe> getRecipeByPublicKey(String publicKey) {
        return mRecipesDao.getRecipeByPublicKey(publicKey);
    }

    LiveData<List<Recipe>> getRecipes() {
        return mRecipesDao.getAll();
    }

    LiveData<List<Step>> getStepsByRecipe(int recipeId) {
        return mStepsDao.getStepsByRecipe(recipeId);
    }

    // Insert and delete methods need to be run on an asynchronous thread.

    public void deleteFood(Food... foods) {
        new DeleteFoodTask(mStorageDao).execute(foods);
    }

    public void deleteIngredient(Ingredient... ingredients) {
        new DeleteIngredientTask(mIngredientsDao).execute(ingredients);
    }

    public void deleteRecipe(Recipe... recipes) {
        new DeleteRecipeTask(mRecipesDao).execute(recipes);
    }

    public void deleteStep(Step... steps) {
        new DeleteStepTask(mStepsDao).execute(steps);
    }

    public void insertFood(Food... foods) {
        new InsertFoodTask(mStorageDao).execute(foods);
    }

    public void insertIngredient(Ingredient... ingredients) {
        new InsertIngredientTask(mIngredientsDao).execute(ingredients);
    }

    public void insertRecipe(Recipe recipe, OnRecipeInsertListener listener) {
        new InsertRecipeTask(mRecipesDao, listener).execute(recipe);
    }

    public void insertStep(Step... steps) {
        new InsertStepTask(mStepsDao).execute(steps);
    }
}
