package com.example.kitchen.data.local.async;

import android.os.AsyncTask;

import com.example.kitchen.data.local.daos.IngredientsDao;
import com.example.kitchen.data.local.entities.Ingredient;

public class InsertIngredientTask extends AsyncTask<Ingredient, Void, Void> {
    private final IngredientsDao mAsyncTaskDao;

    public InsertIngredientTask(IngredientsDao dao) {
        mAsyncTaskDao = dao;
    }

    @Override
    protected Void doInBackground(Ingredient... ingredients) {
        for (Ingredient ingredient : ingredients) {
            if (ingredient != null) {
                long id = mAsyncTaskDao.insert(ingredient);
                if (id == -1) {
                    mAsyncTaskDao.update(ingredient);
                }
            }
        }
        return null;
    }
}
