package org.volkszaehler.volkszaehlerapp.stetho;

import android.content.Context;

import okhttp3.OkHttpClient;

public class StethoHelper {
    public void init(Context context) {
        // Noop
    }

    public OkHttpClient.Builder configureInterceptor(OkHttpClient.Builder httpClient) {
        return httpClient;
    }
}
