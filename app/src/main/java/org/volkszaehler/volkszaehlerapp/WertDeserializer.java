package org.volkszaehler.volkszaehlerapp;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import org.volkszaehler.volkszaehlerapp.model.ResponseWert;

import java.lang.reflect.Type;

public class WertDeserializer implements JsonDeserializer<ResponseWert> {
    @Override
    public ResponseWert deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonArray array = json.getAsJsonArray();
        return new ResponseWert(array.get(0).getAsDouble(), array.get(1).getAsFloat());

    }
}
