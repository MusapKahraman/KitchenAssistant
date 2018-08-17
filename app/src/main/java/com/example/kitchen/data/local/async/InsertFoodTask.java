package com.example.kitchen.data.local.async;

import android.os.AsyncTask;

import com.example.kitchen.data.local.daos.StorageDao;
import com.example.kitchen.data.local.entities.Food;

public class InsertFoodTask extends AsyncTask<Food, Void, Void> {
    private final StorageDao mAsyncTaskDao;

    public InsertFoodTask(StorageDao dao) {
        mAsyncTaskDao = dao;
    }

    @Override
    protected Void doInBackground(Food... foods) {
        for (Food food : foods) {
            if (food != null) {
                long id = mAsyncTaskDao.insertFood(food);
                if (id == -1) {
                    mAsyncTaskDao.update(food);
                }
            }
        }
        return null;
    }
}
