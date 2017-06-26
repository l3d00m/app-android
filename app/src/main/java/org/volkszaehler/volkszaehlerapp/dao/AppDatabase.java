package org.volkszaehler.volkszaehlerapp.dao;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import org.volkszaehler.volkszaehlerapp.generic.Entity;

@Database(entities = {Entity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract EntityDao entityDao();
}

