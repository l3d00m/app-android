package org.volkszaehler.volkszaehlerapp.generic;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.PrimaryKey;

@android.arch.persistence.room.Entity(tableName = "entity")
public class Entity {
    @PrimaryKey
    private String name;
    @ColumnInfo(name = "friendly_name")
    private String friendlyName;
    private String unit;
    private boolean hasConsumption;
    private int scale;

    public Entity() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public boolean hasConsumption() {
        return hasConsumption;
    }

    public void setHasConsumption(boolean hasConsumption) {
        this.hasConsumption = hasConsumption;
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }
}
