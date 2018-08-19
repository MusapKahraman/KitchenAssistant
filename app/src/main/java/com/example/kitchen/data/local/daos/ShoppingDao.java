/*
 * Reference
 * https://stackoverflow.com/questions/45677230/android-room-persistence-library-upsert/48641762#48641762
 */

package com.example.kitchen.data.local.daos;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.kitchen.data.local.entities.Ware;

import java.util.List;

@Dao
public interface ShoppingDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Ware ware);

    @Update
    void update(Ware ware);

    @Delete
    void delete(Ware ware);

    @Query("SELECT * from shopping_list")
    LiveData<List<Ware>> getShoppingList();

}
