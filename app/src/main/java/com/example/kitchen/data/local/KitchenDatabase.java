package com.example.kitchen.data.local;

import android.content.Context;

import com.example.kitchen.data.local.daos.IngredientsDao;
import com.example.kitchen.data.local.daos.RecipesDao;
import com.example.kitchen.data.local.daos.ShoppingDao;
import com.example.kitchen.data.local.daos.StepsDao;
import com.example.kitchen.data.local.daos.StorageDao;
import com.example.kitchen.data.local.entities.Food;
import com.example.kitchen.data.local.entities.Ingredient;
import com.example.kitchen.data.local.entities.Recipe;
import com.example.kitchen.data.local.entities.Step;
import com.example.kitchen.data.local.entities.Ware;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Recipe.class, Ingredient.class, Step.class, Food.class, Ware.class},
        version = 1, exportSchema = false)
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

    public abstract IngredientsDao ingredientsDao();

    public abstract StepsDao stepsDao();

    public abstract StorageDao storageDao();

    public abstract ShoppingDao shoppingDao();
}
