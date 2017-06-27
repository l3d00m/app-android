package org.volkszaehler.volkszaehlerapp.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import org.volkszaehler.volkszaehlerapp.generic.Channel;

import java.util.List;

import io.reactivex.Flowable;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface ChannelDao {
    @Query("SELECT * FROM channel")
    Flowable<List<Channel>> getAll();

    @Insert(onConflict = REPLACE)
    void insertAll(List<Channel> entities);

    @Query("DELETE FROM channel")
    void nukeTable();
}
