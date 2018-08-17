package com.example.kitchen.data.local.async;

import android.os.AsyncTask;

import com.example.kitchen.data.local.OnRecipeInsertListener;
import com.example.kitchen.data.local.daos.RecipesDao;
import com.example.kitchen.data.local.entities.Recipe;

public class InsertRecipeTask extends AsyncTask<Recipe, Void, Void> {
    private final RecipesDao mAsyncTaskDao;
    private final OnRecipeInsertListener listener;

    public InsertRecipeTask(RecipesDao dao, OnRecipeInsertListener listener) {
        mAsyncTaskDao = dao;
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(Recipe... recipes) {
        for (Recipe recipe : recipes) {
            if (recipe != null) {
                long id = mAsyncTaskDao.insert(recipe);
                if (id == -1) {
                    mAsyncTaskDao.update(recipe);
                    listener.onRecipeInsert(recipe.id);
                } else {
                    listener.onRecipeInsert(id);
                }
            }
        }
        return null;
    }
}
