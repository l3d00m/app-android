package org.volkszaehler.volkszaehlerapp.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseRoot {
    public String version;

    @SerializedName("data")
    @Nullable
    public List<ChannelValuesReponse> values;

    @SerializedName("entities")
    @Nullable
    public List<ChannelMetaResponse> infos;

    @SerializedName("capabilities")
    @Nullable
    public CapabilitiesResponse capabilities;

    @SerializedName("exception")
    @Nullable
    public ResponseException exception;
}
