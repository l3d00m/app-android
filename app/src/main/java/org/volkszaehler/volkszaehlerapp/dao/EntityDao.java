package org.volkszaehler.volkszaehlerapp.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import org.volkszaehler.volkszaehlerapp.generic.Entity;

import java.util.List;

import io.reactivex.Flowable;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface EntityDao {
    @Query("SELECT * FROM entity")
    Flowable<List<Entity>> getAll();

    @Insert(onConflict = REPLACE)
    void insertAll(List<Entity> entities);

    @Query("DELETE FROM entity")
    void nukeTable();
}
