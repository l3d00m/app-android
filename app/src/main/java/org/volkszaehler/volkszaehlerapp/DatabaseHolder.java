package org.volkszaehler.volkszaehlerapp;

import android.arch.persistence.room.Room;
import android.content.Context;

import org.volkszaehler.volkszaehlerapp.dao.AppDatabase;

public class DatabaseHolder {
    private static AppDatabase db;

    public static AppDatabase getInstance(Context context) {
        if (db == null) {
            db = Room.databaseBuilder(context, AppDatabase.class, "app-databasee").build();
        }
        return db;
    }


}
