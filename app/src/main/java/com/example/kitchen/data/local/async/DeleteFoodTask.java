package com.example.kitchen.data.local.async;

import android.os.AsyncTask;

import com.example.kitchen.data.local.daos.StorageDao;
import com.example.kitchen.data.local.entities.Food;

public class DeleteFoodTask extends AsyncTask<Food, Void, Void> {
    private final StorageDao mAsyncTaskDao;

    public DeleteFoodTask(StorageDao dao) {
        mAsyncTaskDao = dao;
    }

    @Override
    protected Void doInBackground(Food... foods) {
        for (Food food : foods) {
            if (food != null)
                mAsyncTaskDao.deleteFood(food);
        }
        return null;
    }
}
