package org.volkszaehler.volkszaehlerapp.model;

public class ResponseValue {
    public long timestamp;
    public double value;

    public ResponseValue(long timestamp, double value) {
        this.timestamp = timestamp;
        this.value = value;
    }
}
