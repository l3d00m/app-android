package org.volkszaehler.volkszaehlerapp.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ChannelValuesReponse {
    @SerializedName("tuples")
    public List<ResponseWert> werte;
    @SerializedName("uuid")
    public String uuid;
    @SerializedName("min")
    public ResponseWert minWerte;
    @SerializedName("max")
    public ResponseWert maxWerte;
    @SerializedName("average")
    public Double average;
    @SerializedName("consumption")
    public Double consumption;
    @SerializedName("rows")
    public Integer rows;
}
