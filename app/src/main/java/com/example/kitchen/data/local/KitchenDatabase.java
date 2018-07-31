package com.example.kitchen.data.local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.example.kitchen.data.local.daos.RecipesDao;
import com.example.kitchen.data.local.entities.Recipe;

@Database(entities = {Recipe.class}, version = 1, exportSchema = false)
public abstract class KitchenDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "kitchen_database";
    private static KitchenDatabase sInstance;

    public static KitchenDatabase getDatabase(final Context context) {
        if (sInstance == null) {
            synchronized (KitchenDatabase.class) {
                if (sInstance == null) {
                    sInstance = Room.databaseBuilder(context.getApplicationContext(), KitchenDatabase.class, DATABASE_NAME).build();
                }
            }
        }
        return sInstance;
    }

    public abstract RecipesDao recipesDao();
}
