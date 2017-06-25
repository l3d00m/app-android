package org.volkszaehler.volkszaehlerapp.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ChannelMetaResponse {
    @SerializedName("uuid")
    public String uuid;

    @SerializedName("type")
    public String type;

    @SerializedName("color")
    public String color;

    @SerializedName("fillstyle")
    public Integer fillstyle;

    @SerializedName("public")
    public Boolean isPublic;

    @SerializedName("style")
    public String style;

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

    @SerializedName("resolution")
    public String resolution;

    // Only for groups
    @SerializedName("children")
    public List<ChannelMetaResponse> children;
}
