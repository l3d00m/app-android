package org.volkszaehler.volkszaehlerapp.model;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;

public class PropertyResponse {
    public String type;
    public String pattern;
    public Object min;
    public int max;
    public List<String> options = null;
    public String name;
    @SerializedName("translation")
    public HashMap<String, String> translations;
}
