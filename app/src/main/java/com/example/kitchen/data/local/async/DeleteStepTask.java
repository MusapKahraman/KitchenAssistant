package com.example.kitchen.data.local.async;

import android.os.AsyncTask;

import com.example.kitchen.data.local.daos.StepsDao;
import com.example.kitchen.data.local.entities.Step;

public class DeleteStepTask extends AsyncTask<Step, Void, Void> {
    private final StepsDao mAsyncTaskDao;

    public DeleteStepTask(StepsDao dao) {
        mAsyncTaskDao = dao;
    }

    @Override
    protected Void doInBackground(Step... steps) {
        for (Step step : steps) {
            if (step != null)
                mAsyncTaskDao.delete(step);
        }
        return null;
    }
}
