package org.volkszaehler.volkszaehlerapp.generic;

import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import org.volkszaehler.volkszaehlerapp.model.ChannelMetaResponse.Style;

@android.arch.persistence.room.Entity
public class Channel {
    // Meta Info
    @PrimaryKey
    private String uuid;
    private String description;
    private String type;
    private String color;
    private boolean isPublic;
    @Ignore //fixme("remove ignore by using a custom type converter")
    private Style style;
    private String title;
    private double cost;
    private double resolution;
    private double initialConsumption;

    // ChannelValues
    @Ignore
    private double maxValue;
    @Ignore
    private double minValue;
    @Ignore
    private double value;
    @Ignore
    private long time;
    @Ignore
    private double average;
    @Ignore
    private double consumption;

    public Channel() {
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
    }

    public double getMinValue() {
        return minValue;
    }

    public void setMinValue(double minValue) {
        this.minValue = minValue;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
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

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public double getResolution() {
        return resolution;
    }

    public void setResolution(double resolution) {
        this.resolution = resolution;
    }

    public double getInitialConsumption() {
        return initialConsumption;
    }

    public void setInitialConsumption(double initialConsumption) {
        this.initialConsumption = initialConsumption;
    }
}
