package com.example.kitchen.data.local.async;

import android.os.AsyncTask;

import com.example.kitchen.data.local.daos.ShoppingDao;
import com.example.kitchen.data.local.entities.Ware;

public class DeleteWareTask extends AsyncTask<Ware, Void, Void> {
    private final ShoppingDao mAsyncTaskDao;

    public DeleteWareTask(ShoppingDao dao) {
        mAsyncTaskDao = dao;
    }

    @Override
    protected Void doInBackground(Ware... wares) {
        for (Ware ware : wares) {
            if (ware != null)
                mAsyncTaskDao.delete(ware);
        }
        return null;
    }
}
