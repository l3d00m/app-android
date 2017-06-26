package org.volkszaehler.volkszaehlerapp.generic;

import org.volkszaehler.volkszaehlerapp.model.ChannelMetaResponse.Style;
import org.volkszaehler.volkszaehlerapp.model.ChannelMetaResponse.Type;

public class Channel {
    // Meta Info
    private String uuid;
    private Type type;
    private String color;
    private boolean isPublic;
    private Style style;
    private String title;

    // ChannelValues
    private float maxWert;
    private float minWert;
    private float wert;
    private double average;
    private double consumption;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public float getMaxWert() {
        return maxWert;
    }

    public void setMaxWert(float maxWert) {
        this.maxWert = maxWert;
    }

    public float getMinWert() {
        return minWert;
    }

    public void setMinWert(float minWert) {
        this.minWert = minWert;
    }

    public float getWert() {
        return wert;
    }

    public void setWert(float wert) {
        this.wert = wert;
    }

    public double getAverage() {
        return average;
    }

    public void setAverage(double average) {
        this.average = average;
    }

    public double getConsumption() {
        return consumption;
    }

    public void setConsumption(double consumption) {
        this.consumption = consumption;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null &&
                obj.getClass() == this.getClass() &&
                ((Channel) obj).getUuid().equals(this.getUuid());
    }

    @Override
    public int hashCode() {
        int result = 5;
        int random = 87;
        result = random * result + (getUuid() != null ? getUuid().hashCode() : 0);
        return result;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public boolean getPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public Style getStyle() {
        return style;
    }

    public void setStyle(Style style) {
        this.style = style;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
