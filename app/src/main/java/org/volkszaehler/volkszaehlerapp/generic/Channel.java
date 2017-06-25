package org.volkszaehler.volkszaehlerapp.generic;

public class Channel {
    // Meta Info
    private String uuid;
    private String type;
    private String color;
    private Integer fillstyle;
    private Boolean isPublic;
    private String style;
    private String title;
    private String yaxis;

    // ChannelValues
    private float maxWert;
    private float minWert;
    private float wert;
    private double average;
    private double consumption;


    public String getUuid() {
        return uuid;
    }

    public float getMaxWert() {
        return maxWert;
    }

    public float getMinWert() {
        return minWert;
    }

    public float getWert() {
        return wert;
    }

    public double getAverage() {
        return average;
    }

    public double getConsumption() {
        return consumption;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setMaxWert(float maxWert) {
        this.maxWert = maxWert;
    }

    public void setMinWert(float minWert) {
        this.minWert = minWert;
    }

    public void setWert(float wert) {
        this.wert = wert;
    }

    public void setAverage(double average) {
        this.average = average;
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

    public Integer getFillstyle() {
        return fillstyle;
    }

    public void setFillstyle(Integer fillstyle) {
        this.fillstyle = fillstyle;
    }

    public Boolean getPublic() {
        return isPublic;
    }

    public void setPublic(Boolean aPublic) {
        isPublic = aPublic;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYaxis() {
        return yaxis;
    }

    public void setYaxis(String yaxis) {
        this.yaxis = yaxis;
    }
}
