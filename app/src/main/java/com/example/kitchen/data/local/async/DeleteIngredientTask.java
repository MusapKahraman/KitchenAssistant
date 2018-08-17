package com.example.kitchen.data.local.async;

import android.os.AsyncTask;

import com.example.kitchen.data.local.daos.IngredientsDao;
import com.example.kitchen.data.local.entities.Ingredient;

public class DeleteIngredientTask extends AsyncTask<Ingredient, Void, Void> {
    private final IngredientsDao mAsyncTaskDao;

    public DeleteIngredientTask(IngredientsDao dao) {
        mAsyncTaskDao = dao;
    }

    @Override
    protected Void doInBackground(Ingredient... ingredients) {
        for (Ingredient ingredient : ingredients) {
            if (ingredient != null)
                mAsyncTaskDao.deleteIngredient(ingredient);
        }
        return null;
    }
}
