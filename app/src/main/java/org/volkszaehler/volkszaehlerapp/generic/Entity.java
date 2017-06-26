package org.volkszaehler.volkszaehlerapp.generic;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import org.volkszaehler.volkszaehlerapp.model.ChannelMetaResponse.Style;

@android.arch.persistence.room.Entity
public class Entity {
    @PrimaryKey
    private String name;
    @ColumnInfo(name = "friendly_name")
    private String friendlyName;
    @Ignore //fixme
    private Style style;
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

    public Style getStyle() {
        return style;
    }

    public void setStyle(Style style) {
        this.style = style;
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
