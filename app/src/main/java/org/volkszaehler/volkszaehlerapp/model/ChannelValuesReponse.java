package org.volkszaehler.volkszaehlerapp.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ChannelValuesReponse {
    @SerializedName("tuples")
    public List<ResponseValue> werte;
    @SerializedName("uuid")
    public String uuid;
    @SerializedName("min")
    public ResponseValue minWerte;
    @SerializedName("max")
    public ResponseValue maxWerte;
    @SerializedName("average")
    public double average;
    @SerializedName("consumption")
    public double consumption;
}
