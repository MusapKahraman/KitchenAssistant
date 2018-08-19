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

import com.example.kitchen.data.local.entities.Food;

import java.util.List;

@Dao
public interface StorageDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Food food);

    @Update
    void update(Food food);

    @Delete
    void delete(Food food);

    @Query("SELECT * from storage")
    LiveData<List<Food>> getStorage();

}
