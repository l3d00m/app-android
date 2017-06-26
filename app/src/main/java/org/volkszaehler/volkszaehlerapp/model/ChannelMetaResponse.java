package org.volkszaehler.volkszaehlerapp.model;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ChannelMetaResponse {
    @NonNull
    @SerializedName("uuid")
    public String uuid;

    @SerializedName("type")
    public String type;

    @SerializedName("color")
    public String color;

    @SerializedName("fillstyle")
    public float fillstyle;

    @SerializedName("resolution")
    public float resolution;

    @SerializedName("cost")
    public float cost;

    @SerializedName("initialconsumption")
    public float initialConsumption;

    @SerializedName("public")
    public boolean isPublic;

    @SerializedName("style")
    public Style style;

    @SerializedName("title")
    public String title;

    @SerializedName("yaxis")
    public String yaxis;

    @SerializedName("description")
    public String description;

    @SerializedName("owner")
    public String owner;

    @SerializedName("link")
    public String link;

    @SerializedName("unit")
    public String unit;

    @SerializedName("active")
    public boolean active;

    // Only for groups
    @SerializedName("children")
    public List<ChannelMetaResponse> children;

    public enum Style {
        @SerializedName("lines")
        LINES,
        @SerializedName("steps")
        STEPS,
        @SerializedName("states")
        STATES,
        @SerializedName("points")
        POINTS
    }

    public enum Type {
        @SerializedName("group")
        GROUP,
        @SerializedName("power")
        POWER_S0,
        @SerializedName("powersensor")
        POWERSENSOR,
        @SerializedName("electric meter")
        ABSOLUTE_METER_READING,
        @SerializedName("current")
        CURRENT_METER,
        @SerializedName("gas")
        GAS_S0,
        @SerializedName("gas meter")
        GAS_READINGS,
        @SerializedName("heat")
        HEAT_METER,
        @SerializedName("heatsensor")
        HEAT_SENSOR,
        @SerializedName("temperature")
        TEMPERATURE,
        @SerializedName("water")
        WATER,
        @SerializedName("flow")
        WATER_FLOW_RATE,
        @SerializedName("pressure")
        PRESSURE,
        @SerializedName("humidity")
        HUMIDITY,
    }
}
