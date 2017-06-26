package org.volkszaehler.volkszaehlerapp;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import org.volkszaehler.volkszaehlerapp.model.ResponseValue;

import java.lang.reflect.Type;

public class ValueDeserializer implements JsonDeserializer<ResponseValue> {
    @Override
    public ResponseValue deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonArray array = json.getAsJsonArray();
        return new ResponseValue(array.get(0).getAsLong(), array.get(1).getAsFloat());

    }
}
