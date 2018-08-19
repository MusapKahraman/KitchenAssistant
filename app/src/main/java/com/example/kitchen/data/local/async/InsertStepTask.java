package com.example.kitchen.data.local.async;

import android.os.AsyncTask;

import com.example.kitchen.data.local.daos.StepsDao;
import com.example.kitchen.data.local.entities.Step;

public class InsertStepTask extends AsyncTask<Step, Void, Void> {
    private final StepsDao mAsyncTaskDao;

    public InsertStepTask(StepsDao dao) {
        mAsyncTaskDao = dao;
    }

    @Override
    protected Void doInBackground(Step... steps) {
        for (Step step : steps) {
            if (step != null) {
                long id = mAsyncTaskDao.insert(step);
                if (id == -1) {
                    mAsyncTaskDao.update(step);
                }
            }
        }
        return null;
    }
}
