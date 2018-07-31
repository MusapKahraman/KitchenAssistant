package com.example.kitchen.data;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.example.kitchen.data.local.KitchenDatabase;
import com.example.kitchen.data.local.daos.RecipesDao;
import com.example.kitchen.data.local.entities.Recipe;

import java.util.List;

class KitchenRepository {
    // Define data access objects
    private final RecipesDao mRecipesDao;

    KitchenRepository(Application application) {
        KitchenDatabase db = KitchenDatabase.getDatabase(application);
        mRecipesDao = db.recipesDao();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.

    LiveData<Recipe> getRecipe(String title) {
        return mRecipesDao.getRecipe(title);
    }

    LiveData<List<Recipe>> getAllRecipes() {
        return mRecipesDao.getAll();
    }

    // Insert and delete methods need to be run on an asynchronous thread.

    public void insertRecipes(Recipe... recipes) {
        new InsertRecipeTask(mRecipesDao).execute(recipes);
    }

    public void deleteRecipes(Recipe... recipes) {
        new DeleteRecipeTask(mRecipesDao).execute(recipes);
    }

    private static class InsertRecipeTask extends AsyncTask<Recipe, Void, Void> {
        private final RecipesDao mAsyncTaskDao;

        InsertRecipeTask(RecipesDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Recipe... recipes) {
            for (Recipe recipe : recipes) {
                if (recipe != null)
                    mAsyncTaskDao.insertRecipe(recipe);
            }
            return null;
        }
    }

    private static class DeleteRecipeTask extends AsyncTask<Recipe, Void, Void> {
        private final RecipesDao mAsyncTaskDao;

        DeleteRecipeTask(RecipesDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Recipe... recipes) {
            for (Recipe recipe : recipes) {
                if (recipe != null)
                    mAsyncTaskDao.deleteRecipe(recipe);
            }
            return null;
        }
    }
}
