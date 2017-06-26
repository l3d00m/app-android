package org.volkszaehler.volkszaehlerapp.model;

import com.google.gson.annotations.SerializedName;

import org.volkszaehler.volkszaehlerapp.model.ChannelMetaResponse.Style;

import java.util.HashMap;
import java.util.List;

public class EntityResponse {
    public List<String> required = null;
    public List<String> optional = null;
    public String interpreter;
    public Style style;
    public String model;
    public String unit;
    public String icon;
    public boolean hasConsumption;
    public int scale;
    public String name;
    @SerializedName("translation")
    public HashMap<String, String> translations;
}