package com.example.kitchen.data.local.async;

import android.os.AsyncTask;

import com.example.kitchen.data.local.daos.ShoppingDao;
import com.example.kitchen.data.local.entities.Ware;

public class InsertWareTask extends AsyncTask<Ware, Void, Void> {
    private final ShoppingDao mAsyncTaskDao;

    public InsertWareTask(ShoppingDao dao) {
        mAsyncTaskDao = dao;
    }

    @Override
    protected Void doInBackground(Ware... wares) {
        for (Ware ware : wares) {
            if (ware != null) {
                long id = mAsyncTaskDao.insert(ware);
                if (id == -1) {
                    mAsyncTaskDao.update(ware);
                }
            }
        }
        return null;
    }
}
