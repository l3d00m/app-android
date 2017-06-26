package org.volkszaehler.volkszaehlerapp.dao;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import org.volkszaehler.volkszaehlerapp.generic.Channel;
import org.volkszaehler.volkszaehlerapp.generic.Entity;

@Database(entities = {Entity.class, Channel.class}, version = 4)
public abstract class AppDatabase extends RoomDatabase {
    public abstract EntityDao entityDao();

    public abstract ChannelDao channelMetaDao();
}

