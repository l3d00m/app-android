package org.volkszaehler.volkszaehlerapp.model;

public class ResponseWert {
    public double timestamp;
    public float wert;

    public ResponseWert(double timestamp, float wert) {
        this.timestamp = timestamp;
        this.wert = wert;
    }
}
