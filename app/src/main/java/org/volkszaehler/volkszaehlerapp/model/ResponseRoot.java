package org.volkszaehler.volkszaehlerapp.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseRoot {
    public String version;
    @SerializedName("data")
    public List<ChannelValuesReponse> values;
    @SerializedName("channels")
    public List<ChannelMetaResponse> infos;
    public ResponseException exception;
}
