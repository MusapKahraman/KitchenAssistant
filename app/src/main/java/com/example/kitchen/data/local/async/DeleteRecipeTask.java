package com.example.kitchen.data.local.async;

import android.os.AsyncTask;

import com.example.kitchen.data.local.daos.RecipesDao;
import com.example.kitchen.data.local.entities.Recipe;

public class DeleteRecipeTask extends AsyncTask<Recipe, Void, Void> {
    private final RecipesDao mAsyncTaskDao;

    public DeleteRecipeTask(RecipesDao dao) {
        mAsyncTaskDao = dao;
    }

    @Override
    protected Void doInBackground(Recipe... recipes) {
        for (Recipe recipe : recipes) {
            if (recipe != null)
                mAsyncTaskDao.delete(recipe);
        }
        return null;
    }
}
